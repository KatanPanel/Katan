package org.katan.service.blueprint.parser

import com.typesafe.config.Config
import com.typesafe.config.ConfigException
import com.typesafe.config.ConfigFactory
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

internal typealias BlueprintParser = Parser

// TODO better error handling
internal class Parser(private val supportedProperties: List<Property> = AllSupportedProperties) {

    private val json: Json = Json {
        ignoreUnknownKeys = false
    }
    private val parseOptions = ConfigParseOptions.defaults().setSyntax(ConfigSyntax.CONF)

    fun parse(input: String): BlueprintSpec {
        val transformed = read(input)
        return json.decodeFromJsonElement<BlueprintSpecImpl>(transformed)
    }

    internal fun read(input: String): JsonObject {
        val output = mutableMapOf<String, JsonElement>()

        try {
            val config = ConfigFactory.parseString(input, parseOptions)

            // Iterate over supported properties because some root level properties have required
            // constraints. Unknown nodes are purposefully ignored.
            for (property in supportedProperties) {
                val node = config.getValueOrNull(property.qualifiedName)
                if (node != null) {
                    checkPropertyAndNodeKindEquality(property, node)
                }

                val unwrappedValue = node?.unwrapped()
                val actualKind = if (node == null) null else kindFromNodeValueType(node.valueType())

                validate(property, actualKind, unwrappedValue)
                output[property.name] = element(unwrappedValue)
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

    private fun validate(property: Property, actualKind: KClass<out PropertyKind>?, value: Any?) {
        for (constraint in property.constraints) {
            try {
                constraint.check(property, actualKind, value)
            } catch (e: IllegalStateException) {
                throw ConstraintViolationException(e.message, property, constraint)
            }
        }
    }

    private fun element(unwrappedValue: Any?): JsonElement = when (unwrappedValue) {
        null -> JsonNull
        is String -> JsonPrimitive(unwrappedValue)
        is Number -> JsonPrimitive(unwrappedValue)
        is Boolean -> JsonPrimitive(unwrappedValue)
        is List<*> -> JsonArray(unwrappedValue.mapNotNull { value -> element(value as Any) })
        else -> error("Unsupported element: $unwrappedValue")
    }

    private fun nodeValueTypeFromKind(kind: PropertyKind) = when (kind) {
        is PropertyKind.Literal -> ConfigValueType.STRING
        is PropertyKind.Numeric -> ConfigValueType.NUMBER
        is PropertyKind.TrueOrFalse -> ConfigValueType.BOOLEAN
        is PropertyKind.Multiple -> ConfigValueType.LIST
        is PropertyKind.Null -> ConfigValueType.NULL
        else -> error("Unsupported property kind: $kind")
    }

    private fun kindFromNodeValueType(type: ConfigValueType) = when (type) {
        ConfigValueType.STRING -> PropertyKind.Literal::class
        ConfigValueType.NUMBER -> PropertyKind.Numeric::class
        ConfigValueType.BOOLEAN -> PropertyKind.TrueOrFalse::class
        ConfigValueType.LIST -> PropertyKind.Multiple::class
        ConfigValueType.OBJECT -> PropertyKind.Object::class
        ConfigValueType.NULL -> PropertyKind.Null::class
        else -> error("Unsupported node value type: $type")
    }

    private fun checkPropertyAndNodeKindEquality(property: Property, node: ConfigValue) {
        val inputNodeType = node.valueType()

        if (property.kind !is PropertyKind.Mixed) {
            checkPropertyAndNodeKindEquality(property.kind, inputNodeType)
            return
        }

        if (property.kind.isAllTypesSupported) {
            return
        }

        var anyMatch = false
        for (kind in property.kind.kinds) {
            try {
                checkPropertyAndNodeKindEquality(kind, inputNodeType)
                anyMatch = true
                break
            } catch (_: Throwable) {
            }
        }

        if (!anyMatch) {
            val kinds = property.kind.kinds.map { kind -> kind::class.java.simpleName }
            val actual = kindFromNodeValueType(inputNodeType).java.simpleName
            throw NoMatchesForMixedProperty(
                "No matching kinds for mixed property. Expected any of $kinds kinds. Actual: $actual",
                property
            )
        }

        return
    }

    private fun checkPropertyAndNodeKindEquality(kind: PropertyKind, nodeType: ConfigValueType) {
        val targetNodeType = nodeValueTypeFromKind(kind)
        if (nodeType != targetNodeType) {
            error("Wrong value type. Expected: $targetNodeType, given: $nodeType")
        }
    }

    private fun Config.getValueOrNull(path: String): ConfigValue? = root()[path]
}
