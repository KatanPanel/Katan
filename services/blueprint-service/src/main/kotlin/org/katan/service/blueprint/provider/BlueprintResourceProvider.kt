package org.katan.service.blueprint.provider

import org.katan.model.blueprint.RawBlueprint

interface BlueprintResourceProvider {

    suspend fun provideFrom(source: BlueprintResource): RawBlueprint

}