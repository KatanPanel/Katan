package org.katan.service.blueprint.provider

import com.typesafe.config.ConfigException
import com.typesafe.config.ConfigFactory
import com.typesafe.config.ConfigParseOptions
import com.typesafe.config.ConfigSyntax
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.get
import io.ktor.http.HttpStatusCode
import io.ktor.http.URLBuilder
import io.ktor.http.appendPathSegments
import kotlinx.serialization.SerializationException
import kotlinx.serialization.hocon.Hocon
import kotlinx.serialization.hocon.decodeFromConfig
import org.apache.logging.log4j.LogManager
import org.apache.logging.log4j.Logger
import org.katan.model.blueprint.RawBlueprint
import org.katan.service.blueprint.RawBlueprintParseException
import org.katan.service.blueprint.model.ProvidedRawBlueprint
import org.katan.service.blueprint.model.ProvidedRawBlueprintAsset
import org.katan.service.blueprint.model.ProvidedRawBlueprintMain
import org.katan.service.blueprint.model.RawBlueprintImpl

class GithubBlueprintResource(val url: String) : BlueprintResource

internal class GithubBlueprintResourceProvider(
    val httpClient: HttpClient
) : BlueprintResourceProvider {

    companion object {
        private val logger: Logger =
            LogManager.getLogger(GithubBlueprintResourceProvider::class.java)
        const val NAME = "github"
    }

    private val hocon = Hocon {
        useConfigNamingConvention = true
    }

    override val id: String get() = NAME

    override suspend fun canProvideFrom(url: String): Boolean {
        // TODO check for some github urls
        return true
    }

    override suspend fun provideFrom(source: BlueprintResource): ProvidedRawBlueprint? {
        require(source is GithubBlueprintResource)

        logger.info("Download blueprint from $source...")

        val response = httpClient.get(source.url)
        return try {
            val main = readMain(response.body())

            // TODO read all declared assets
            val assets = mutableListOf<ProvidedRawBlueprintAsset>()
            readReadme(main.raw)?.let { assets += it }

            ProvidedRawBlueprint(main, assets.toList())
        } catch (e: SerializationException) {
            throw RawBlueprintParseException(e.message!!)
        } catch (e: ConfigException) {
            throw RawBlueprintParseException(e.message!!)
        }
    }

    override suspend fun provideFrom(url: String): ProvidedRawBlueprint? {
        return provideFrom(GithubBlueprintResource(url))
    }

    private suspend fun readReadme(raw: RawBlueprint): ProvidedRawBlueprintAsset? {
        val name = "README.md"
        val origin = raw.remote.origin!!
        val url = URLBuilder(origin).apply {
            appendPathSegments(name)
        }.buildString()

        val response = runCatching {
            httpClient.get(url)
        }.onFailure {
            logger.error(
                "An error occurred while downloading blueprint asset \"$name\" of \"${raw.name}\" from $url.",
                it
            )
        }.getOrNull() ?: return null

        if (response.status != HttpStatusCode.OK) {
            logger.error("Received non-200 HTTP status code while downloading asset \"$name\" of \"${raw.name}\" from $url: HTTP ${response.status}")
            return null
        }

        return ProvidedRawBlueprintAsset(name, response.body())
    }

    private fun readMain(contents: ByteArray): ProvidedRawBlueprintMain {
        val config = ConfigFactory.parseString(
            contents.decodeToString(),
            ConfigParseOptions.defaults().setSyntax(ConfigSyntax.CONF)
        )
        val result = hocon.decodeFromConfig<RawBlueprintImpl>(config)

        return ProvidedRawBlueprintMain(result, result.remote.main, contents)
    }

}