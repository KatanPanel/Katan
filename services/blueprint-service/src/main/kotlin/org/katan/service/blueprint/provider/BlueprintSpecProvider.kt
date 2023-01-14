package org.katan.service.blueprint.provider

import org.katan.service.blueprint.model.ProvidedRawBlueprint

interface BlueprintSpecProvider {

    val id: String

    suspend fun provide(source: BlueprintSpecSource): ProvidedRawBlueprint?
}
