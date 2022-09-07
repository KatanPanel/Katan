package org.katan.service.blueprint.provider

import org.katan.service.blueprint.model.ProvidedRawBlueprint

interface BlueprintResourceProvider {

    val id: String

    suspend fun canProvideFrom(url: String): Boolean

    suspend fun provideFrom(source: BlueprintResource): ProvidedRawBlueprint?

    suspend fun provideFrom(url: String): ProvidedRawBlueprint?

}