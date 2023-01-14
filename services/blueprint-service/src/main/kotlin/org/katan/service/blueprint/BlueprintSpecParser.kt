package org.katan.service.blueprint

import com.typesafe.config.ConfigException
import com.typesafe.config.ConfigFactory
import com.typesafe.config.ConfigParseOptions
import com.typesafe.config.ConfigSyntax
import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.SerializationException
import kotlinx.serialization.hocon.Hocon
import kotlinx.serialization.hocon.decodeFromConfig
import org.katan.model.blueprint.BlueprintSpec
import org.katan.service.blueprint.model.BlueprintSpecImpl

internal class BlueprintSpecParser {

    @OptIn(ExperimentalSerializationApi::class)
    private val hocon = Hocon {
        useConfigNamingConvention = true
    }

    @Suppress("RedundantSuspendModifier")
    suspend fun parse(input: String): BlueprintSpec {
        return try {
            val config = ConfigFactory.parseString(
                input,
                ConfigParseOptions.defaults().setSyntax(ConfigSyntax.CONF)
            )

            @OptIn(ExperimentalSerializationApi::class)
            hocon.decodeFromConfig<BlueprintSpecImpl>(config)
        } catch (exception: Throwable) {
            val message = when (exception) {
                is SerializationException -> "Failed to deserialize from HOCON config"
                is ConfigException -> "An error occurred while parsing the input"
                else -> "An unexpected error occurred while parsing"
            }

            throw BlueprintSpecParseException(message, exception)
        }
    }
}
