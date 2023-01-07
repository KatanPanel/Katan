package org.katan.service.blueprint.provider

import org.katan.service.blueprint.model.ProvidedRawBlueprint

internal data class CombinedBlueprintResourceProvider(
    val providers: List<BlueprintResourceProvider>
) : BlueprintResourceProvider {

    override val id: String
        get() = error("Cannot get id from CombinedBlueprintResourceProvider")

    override suspend fun canProvideFrom(url: String): Boolean {
        return providers.any { it.canProvideFrom(url) }
    }

    override suspend fun provideFrom(source: BlueprintResource): ProvidedRawBlueprint? {
        for (provider in providers) {
            return provider.provideFrom(source) ?: continue
        }
        return null
    }

    override suspend fun provideFrom(url: String): ProvidedRawBlueprint? {
        for (provider in providers) {
            return provider.provideFrom(url) ?: continue
        }
        return null
    }
}
