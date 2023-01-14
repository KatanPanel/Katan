package org.katan.service.blueprint.provider

import org.katan.model.blueprint.BlueprintSpec
import org.katan.service.blueprint.NoMatchingBlueprintSpecProviderException
import org.katan.service.blueprint.UnsupportedBlueprintSpecSource

internal data class CombinedBlueprintSpecProvider(
    val providers: List<BlueprintSpecProvider>
) : BlueprintSpecProvider {

    override val id: String
        get() = error("Cannot get id from CombinedBlueprintResourceProvider")

    override suspend fun provide(source: BlueprintSpecSource): BlueprintSpec {
        for (provider in providers) {
            runCatching {
                provider.provide(source)
            }.recoverCatching { exception ->
                if (exception is UnsupportedBlueprintSpecSource) {
                    null
                } else {
                    throw exception
                }
            }.getOrNull() ?: continue
        }

        throw NoMatchingBlueprintSpecProviderException()
    }
}
