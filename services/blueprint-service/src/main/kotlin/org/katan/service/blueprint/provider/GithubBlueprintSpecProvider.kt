package org.katan.service.blueprint.provider

import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.get
import org.katan.model.blueprint.BlueprintSpec
import org.katan.service.blueprint.BlueprintSpecNotFound
import org.katan.service.blueprint.BlueprintSpecParser
import org.katan.service.blueprint.UnsupportedBlueprintSpecSource
import java.nio.channels.UnresolvedAddressException

internal class GithubBlueprintSpecProvider(
    val httpClient: HttpClient,
    val blueprintSpecParser: BlueprintSpecParser
) : BlueprintSpecProvider {

    override val id: String get() = "github"

    override suspend fun provide(source: BlueprintSpecSource): BlueprintSpec {
        if (source !is URLBlueprintSpecSource) {
            throw UnsupportedBlueprintSpecSource()
        }

        val response = try {
            httpClient.get(source.url)
        } catch (e: UnresolvedAddressException) {
            throw BlueprintSpecNotFound()
        }

        val contents: String = response.body()
        return blueprintSpecParser.parse(contents)
    }
}
