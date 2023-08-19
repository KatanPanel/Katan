package org.katan.service.blueprint.provider

import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.get
import org.katan.model.blueprint.BlueprintSpec
import org.katan.service.blueprint.BlueprintSpecNotFound
import org.katan.service.blueprint.UnsupportedBlueprintSpecSource
import org.katan.service.blueprint.parser.BlueprintParser
import java.nio.channels.UnresolvedAddressException

@JvmInline
internal value class RemoteBlueprintSpecSource(val url: String) : BlueprintSpecSource

internal class RemoteBlueprintSpecProvider(val httpClient: HttpClient, val parser: BlueprintParser) :
    BlueprintSpecProvider {

    override val id: String get() = "remote"

    override suspend fun provide(source: BlueprintSpecSource): BlueprintSpec {
        if (source !is RemoteBlueprintSpecSource) {
            throw UnsupportedBlueprintSpecSource()
        }

        val response = try {
            httpClient.get(source.url)
        } catch (e: UnresolvedAddressException) {
            throw BlueprintSpecNotFound()
        }

        val contents: String = response.body()
        return parser.parse(contents)
    }
}
