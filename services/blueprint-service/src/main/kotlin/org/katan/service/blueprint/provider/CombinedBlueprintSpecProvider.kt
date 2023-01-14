package org.katan.service.blueprint.provider

import org.katan.service.blueprint.model.ProvidedRawBlueprint

internal data class CombinedBlueprintSpecProvider(
    val providers: List<BlueprintSpecProvider>
) : BlueprintSpecProvider {

    override val id: String
        get() = error("Cannot get id from CombinedBlueprintResourceProvider")

    override suspend fun provide(source: BlueprintSpecSource): ProvidedRawBlueprint? {
        for (provider in providers) {
            return provider.provide(source) ?: continue
        }
        return null
    }
}
