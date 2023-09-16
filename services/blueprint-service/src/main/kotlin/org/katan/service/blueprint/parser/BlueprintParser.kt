package org.katan.service.blueprint.parser

import com.typesafe.config.ConfigException
import com.typesafe.config.ConfigFactory
import com.typesafe.config.ConfigList
import com.typesafe.config.ConfigObject
import com.typesafe.config.ConfigParseOptions
import com.typesafe.config.ConfigSyntax
import com.typesafe.config.ConfigValue
import com.typesafe.config.ConfigValueType
import kotlinx.serialization.SerializationException
import kotlinx.serialization.json.JsonArray
import kotlinx.serialization.json.JsonElement
import kotlinx.serialization.json.JsonNull
import kotlinx.serialization.json.JsonObject
import kotlinx.serialization.json.JsonPrimitive
import org.katan.model.blueprint.BlueprintSpec
import org.katan.model.blueprint.BlueprintSpecBuild
import org.katan.model.blueprint.BlueprintSpecImage
import org.katan.model.blueprint.BlueprintSpecInstance
import kotlin.reflect.KClass

// TODO detailed error diagnostics
internal class BlueprintParser(private val supportedProperties: List<Property> = AllSupportedProperties) {

    private val parseOptions = ConfigParseOptions.defaults().setSyntax(ConfigSyntax.CONF)
    private val requiredProperties: List<Property> = supportedProperties.filter { property ->
        property.constraints.any { constraint -> constraint is RequiredPropertyConstraint }
    }

    internal fun read(input: String): JsonObject {
        val output = mutableMapOf<String, JsonElement>()

        try {
            val config = ConfigFactory.parseString(input, parseOptions)

            // TODO remove this and iterate over supported properties building a qualified name
            //      allowing RequiredPropertyConstraint work properly :)
            for (requiredProperty in requiredProperties) {
                if (config.hasPathOrNull(requiredProperty.qualifiedName)) {
                    continue
                }

                validate(
                    property = requiredProperty,
                    actualKind = requiredProperty.kind::class,
                    value = null,
                    constraints = listOf(RequiredPropertyConstraint)
                )
            }

            // Iterate over supported properties because some root level properties have required
            // constraints. Unknown nodes are purposefully ignored.
            for ((path, node) in config.root().entries) {
                val element = read(path, node) ?: continue
                output[path] = element
            }
        } catch (exception: Throwable) {
            val message = when (exception) {
                is BlueprintSpecParseException -> throw exception
                is SerializationException -> "Failed to deserialize from HOCON config"
                is ConfigException -> "An error occurred while parsing the input"
                else -> "An unexpected error occurred while parsing"
            }

            throw BlueprintSpecParseException(message, exception)
        }

        return JsonObject(output)
    }

    internal fun read(property: Property, node: ConfigValue, equalityCheck: Boolean): JsonElement {
        if (equalityCheck) checkPropertyAndNodeKindEquality(property, node)

        return when (node.valueType()) {
            ConfigValueType.LIST -> {
                val root = (node as ConfigList)
                val elements = mutableListOf<JsonElement>()
                for (childNode in root) {
                    val childType = childNode.valueType()
                    val childKind = kindFromNodeValueType(childType)
                    if (!property.supports(childKind)) {
                        val multi = (property.kind as PropertyKind.Multiple)
                        val supportedName = multi.supports::class.java.simpleName
                        error("Expected list of $supportedName, got ${childKind.java.simpleName} instead")
                    }

                    // we need to check each value in this list based on supported list types
                    checkPropertyAndNodeKindEquality(childKind, childType)

                    val element = read(property, childNode, false) ?: continue
                    elements.add(element)
                }

                JsonArray(elements.toList())
            }

            ConfigValueType.OBJECT -> {
                val root = (node as ConfigObject)
                val elements = mutableMapOf<String, JsonElement>()
                for ((childKey, childNode) in root.entries) {
                    val qName = property.qualifiedName + PROPERTY_NAME_SEPARATOR + childKey
                    val el = read(qName, childNode)
                        ?: continue

                    elements[childKey] = el
                }

                JsonObject(elements.toMap())
            }

            else -> {
                val unwrappedValue = node.unwrapped()
                val actualKind = kindFromNodeValueType(node.valueType())
                validate(property, actualKind, unwrappedValue)
                primitiveElement(unwrappedValue)
            }
        }
    }

    internal fun read(qualifiedName: String, node: ConfigValue): JsonElement? {
        val property = supportedProperties.firstOrNull { property ->
            property.qualifiedName == qualifiedName
        } ?: return null

        return read(property, node, true)
    }

    fun parse(input: String): BlueprintSpec {
        val value = read(input)
        return transform(value)
    }

    private fun validate(property: Property, actualKind: KClass<out PropertyKind>?, value: Any?, constraints: Iterable<PropertyConstraint> = property.constraints) {
        for (constraint in constraints) {
            try {
                constraint.check(property, actualKind, value)
            } catch (e: IllegalStateException) {
                throw ConstraintViolationException(e.message, property, constraint)
            }
        }
    }

    private fun primitiveElement(unwrappedValue: Any?): JsonElement = when (unwrappedValue) {
        null -> JsonNull
        is String -> JsonPrimitive(unwrappedValue)
        is Number -> JsonPrimitive(unwrappedValue)
        is Boolean -> JsonPrimitive(unwrappedValue)
        else -> error("Unsupported primitive element: $unwrappedValue")
    }

    private fun nodeValueTypeFromKind(kind: KClass<out PropertyKind>) = when (kind) {
        PropertyKind.Literal::class -> ConfigValueType.STRING
        PropertyKind.Numeric::class -> ConfigValueType.NUMBER
        PropertyKind.TrueOrFalse::class -> ConfigValueType.BOOLEAN
        PropertyKind.Multiple::class -> ConfigValueType.LIST
        PropertyKind.Null::class -> ConfigValueType.NULL
        PropertyKind.Struct::class -> ConfigValueType.OBJECT
        else -> error("Unsupported property kind: $kind")
    }

    private fun kindFromNodeValueType(type: ConfigValueType) = when (type) {
        ConfigValueType.STRING -> PropertyKind.Literal::class
        ConfigValueType.NUMBER -> PropertyKind.Numeric::class
        ConfigValueType.BOOLEAN -> PropertyKind.TrueOrFalse::class
        ConfigValueType.LIST -> PropertyKind.Multiple::class
        ConfigValueType.OBJECT -> PropertyKind.Struct::class
        ConfigValueType.NULL -> PropertyKind.Null::class
        else -> error("Unsupported node value type: $type")
    }

    private fun checkPropertyAndNodeKindEquality(property: Property, node: ConfigValue) {
        val inputNodeType = node.valueType()
        val kind = property.kind

        if (kind !is PropertyKind.Mixed) {
            return checkPropertyAndNodeKindEquality(
                kind::class,
                inputNodeType
            )
        }
        if (kind.isAllTypesSupported) return

        val anyMatchingKindAvailable = checkMultipleKinds(inputNodeType, kind.kinds)
        if (!anyMatchingKindAvailable) {
            val kinds = kind.kinds.map { childKind -> childKind::class.java.simpleName }
            val actual = kindFromNodeValueType(inputNodeType).java.simpleName
            throw NoMatchesForMixedProperty(
                "No matching kinds for mixed property. Expected any of $kinds kinds. Actual: $actual",
                property
            )
        }

        return
    }

    private fun checkPropertyAndNodeKindEquality(kind: KClass<out PropertyKind>, nodeType: ConfigValueType) {
        val targetNodeType = nodeValueTypeFromKind(kind)
        if (nodeType != targetNodeType) {
            error("Wrong value type. Expected: $targetNodeType, given: $nodeType")
        }
    }

    private fun checkMultipleKinds(nodeType: ConfigValueType, values: List<PropertyKind>): Boolean {
        var anyMatch = false
        for (childKind in values) {
            try {
                checkPropertyAndNodeKindEquality(childKind::class, nodeType)
                anyMatch = true
                break
            } catch (_: Throwable) {
            }
        }
        return anyMatch
    }

    private fun transform(value: JsonObject): BlueprintSpec = with(value) {
        BlueprintSpec(
            name = string("name"),
            version = string("version"),
            remote = null,
            build = struct("build")?.let { build ->
                BlueprintSpecBuild(
                    entrypoint = build.string("entrypoint"),
                    env = emptyMap(),
                    image = build.getValue("image").let(::elementToImage),
                    instance = build.struct("instance")?.let { instance ->
                        BlueprintSpecInstance(
                            name = instance.string("name")
                        )
                    }
                )
            },
            options = emptyList()
        )
    }

    private fun elementToImage(element: JsonElement): BlueprintSpecImage {
        return when (element) {
            is JsonObject -> BlueprintSpecImage.Ref(
                ref = element.string("ref"),
                tag = element.string("tag")
            )

            is JsonArray -> BlueprintSpecImage.Multiple(
                images = element.map { child ->
                    if (child !is JsonObject) {
                        error("Expected a JsonObject for image multiple child, given $child")
                    }

                    BlueprintSpecImage.Ref(
                        ref = child.string("ref"),
                        tag = child.string("tag")
                    )
                }
            )

            is JsonPrimitive -> BlueprintSpecImage.Identifier(
                id = element.content
            )

            else -> error("Unsupported type")
        }
    }
}
