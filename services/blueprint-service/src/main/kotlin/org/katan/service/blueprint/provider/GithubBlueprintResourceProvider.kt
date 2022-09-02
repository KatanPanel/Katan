package org.katan.service.blueprint.provider

import com.typesafe.config.ConfigFactory
import com.typesafe.config.ConfigParseOptions
import com.typesafe.config.ConfigSyntax
import io.ktor.client.HttpClient
import io.ktor.client.request.get
import io.ktor.client.statement.bodyAsText
import kotlinx.serialization.hocon.Hocon
import kotlinx.serialization.hocon.decodeFromConfig
import org.apache.logging.log4j.LogManager
import org.apache.logging.log4j.Logger
import org.katan.model.blueprint.RawBlueprint
import org.katan.service.blueprint.model.RawBlueprintImpl

class GithubBlueprintResource(val url: String) : BlueprintResource

internal class GithubBlueprintResourceProvider(
    val httpClient: HttpClient
) : BlueprintResourceProvider {

    companion object {
        private val logger: Logger = LogManager.getLogger(GithubBlueprintResourceProvider::class.java)
    }

    private val hocon = Hocon {
        useConfigNamingConvention = true
    }

    override suspend fun provideFrom(source: BlueprintResource): RawBlueprint {
        require(source is GithubBlueprintResource)

        logger.info("Download blueprint from $source...")

        val contents = httpClient.get(source.url)

        val body = contents.bodyAsText()
        val config = ConfigFactory.parseString(body, ConfigParseOptions.defaults().setSyntax(ConfigSyntax.CONF))

        val type = hocon.decodeFromConfig<RawBlueprintImpl>(config)

        logger.info("Body")
        logger.info(type)
        return type
    }

}