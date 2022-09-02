package org.katan.service.blueprint

import org.katan.model.blueprint.Blueprint
import org.katan.model.blueprint.RawBlueprint

interface BlueprintService {

    suspend fun listBlueprints(): List<Blueprint>

    suspend fun getBlueprint(id: Long): Blueprint

    suspend fun listProvided(): List<RawBlueprint>

    suspend fun importBlueprint(url: String): RawBlueprint
}
