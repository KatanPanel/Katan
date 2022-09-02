package org.katan.service.blueprint

import org.katan.model.blueprint.Blueprint
import org.katan.model.blueprint.RawBlueprint

interface BlueprintService {

    suspend fun getBlueprint(id: Long): Blueprint

    suspend fun downloadBlueprint(source: String): RawBlueprint
}
