package org.katan.service.blueprint.provider

import org.katan.model.blueprint.BlueprintSpec
import org.katan.service.blueprint.NoMatchingBlueprintSpecProviderException
import org.katan.service.blueprint.UnsupportedBlueprintSpecSource

internal data class CombinedBlueprintSpecProvider(val providers: List<BlueprintSpecProvider>) : BlueprintSpecProvider {

    override val id: String
        get() = error("Cannot get id from CombinedBlueprintResourceProvider")

    override suspend fun provide(source: BlueprintSpecSource): BlueprintSpec {
        for (provider in providers) {
            try {
                return provider.provide(source)
            } catch (_: UnsupportedBlueprintSpecSource) {
                continue
            }
        }

        throw NoMatchingBlueprintSpecProviderException()
    }
}
