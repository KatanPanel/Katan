package org.katan.service.blueprint

import org.katan.model.blueprint.Blueprint
import org.katan.model.blueprint.BlueprintSpec
import org.katan.model.io.VirtualFile
import org.katan.service.blueprint.model.ImportedBlueprint
import org.katan.service.blueprint.provider.BlueprintSpecSource
import org.katan.service.blueprint.provider.URLBlueprintSpecSource

interface BlueprintService {

    suspend fun listBlueprints(): List<Blueprint>

    suspend fun getSpec(id: Long): BlueprintSpec

    suspend fun getBlueprint(id: Long): Blueprint

    suspend fun importBlueprint(source: BlueprintSpecSource): ImportedBlueprint

    suspend fun readBlueprintAssetContents(id: Long, path: String): Pair<VirtualFile, ByteArray>
}

suspend inline fun BlueprintService.importBlueprint(url: String): ImportedBlueprint =
    importBlueprint(URLBlueprintSpecSource(url))
