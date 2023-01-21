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
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.JsonArray
import kotlinx.serialization.json.JsonElement
import kotlinx.serialization.json.JsonNull
import kotlinx.serialization.json.JsonObject
import kotlinx.serialization.json.JsonPrimitive
import kotlinx.serialization.json.decodeFromJsonElement
import org.katan.model.blueprint.BlueprintSpec
import org.katan.service.blueprint.model.BlueprintSpecImpl
import kotlin.reflect.KClass

// TODO detailed error diagnostics
internal class BlueprintParser(private val supportedProperties: List<Property> = AllSupportedProperties) {

    private val json: Json = Json {
        ignoreUnknownKeys = false
    }
    private val parseOptions = ConfigParseOptions.defaults().setSyntax(ConfigSyntax.CONF)

    internal fun read(input: String): JsonObject {
        val output = mutableMapOf<String, JsonElement>()

        try {
            val config = ConfigFactory.parseString(input, parseOptions)

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

    internal fun read(qualifiedName: String, node: ConfigValue): JsonElement? {
        val property = supportedProperties.firstOrNull { property ->
            property.qualifiedName == qualifiedName
        } ?: error("Property $qualifiedName not found")

        checkPropertyAndNodeKindEquality(property, node)

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

                    val value = childNode.unwrapped()

                    // we need to check each value in this list based on supported list types
                    checkPropertyAndNodeKindEquality(childKind, childType)

                    elements.add(primitiveElement(value))
                }

                JsonArray(elements.toList())
            }
            ConfigValueType.OBJECT -> {
                val root = (node as ConfigObject)
                val elements = mutableMapOf<String, JsonElement>()
                for ((childKey, childNode) in root.entries) {
                    val qName = qualifiedName + PROPERTY_NAME_SEPARATOR + childKey
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

    fun parse(input: String): BlueprintSpec {
        val transformed = read(input)
        return json.decodeFromJsonElement<BlueprintSpecImpl>(transformed)
    }

    private fun validate(property: Property, actualKind: KClass<out PropertyKind>?, value: Any?) {
        for (constraint in property.constraints) {
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

        if (kind !is PropertyKind.Mixed) return checkPropertyAndNodeKindEquality(kind::class, inputNodeType)
        if (kind.isAllTypesSupported) return

        val anyMatchingKindAvailable = checkMultipleKinds(kind, inputNodeType, kind.kinds)
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

    private fun checkMultipleKinds(kind: PropertyKind, nodeType: ConfigValueType, values: List<PropertyKind>): Boolean {
        var anyMatch = false
        for (childKind in values) {
            try {
                checkPropertyAndNodeKindEquality(kind::class, nodeType)
                anyMatch = true
                break
            } catch (_: Throwable) {
            }
        }
        return anyMatch
    }
}
