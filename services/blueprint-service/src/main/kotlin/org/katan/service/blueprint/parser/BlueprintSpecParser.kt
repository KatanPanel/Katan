package org.katan.service.blueprint.parser

import com.typesafe.config.ConfigException
import com.typesafe.config.ConfigFactory
import com.typesafe.config.ConfigObject
import com.typesafe.config.ConfigParseOptions
import com.typesafe.config.ConfigSyntax
import com.typesafe.config.ConfigValue
import kotlinx.serialization.SerializationException
import org.katan.model.blueprint.BlueprintSpec
import org.katan.service.blueprint.parser.SpecProperty.Companion.Name
import org.katan.service.blueprint.parser.SpecProperty.Companion.Version

internal class BlueprintSpecParser {

    private val parseOptions = ConfigParseOptions.defaults().setSyntax(ConfigSyntax.CONF)

    internal fun String.asConfigValue(): ConfigValue {
        return ConfigFactory.parseString(this, parseOptions).root()
    }

    fun parse(input: String): BlueprintSpec {
        val spec: BlueprintSpec
        try {
            val root = input.asConfigValue() as ConfigObject
            root.parseTopLevel()

            for ((key, child) in root.entries)
                parseNode(key, child)
        } catch (exception: Throwable) {
            val message = when (exception) {
                is BlueprintSpecParsingException -> throw exception
                is SerializationException -> "Failed to deserialize from HOCON config"
                is ConfigException -> "An error occurred while parsing the input"
                else -> "An unexpected error occurred while parsing"
            }

            throw BlueprintSpecParsingException(message, exception)
        }

        TODO("Blueprint spec cannot be built for now")
    }

    private fun parseNode(node: String, config: ConfigValue) {
        when (node) {
            "build" -> TODO()
            "options" -> TODO()
            "remote" -> TODO()
            else -> error("Unsupported spec node: $node")
        }
    }

    internal fun ConfigValue.parseTopLevel(): ParsedBlueprintSpecRoot {
        val name by parsing(Name).required().notBlank()
        val version by parsing(Version).required().notBlank()

        return ParsedBlueprintSpecRoot(
            name = name,
            version = version
        )
    }
}
