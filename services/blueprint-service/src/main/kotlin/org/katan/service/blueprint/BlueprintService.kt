package org.katan.service.blueprint

import org.katan.model.blueprint.Blueprint
import org.katan.model.blueprint.RawBlueprint
import org.katan.model.io.VirtualFile
import org.katan.service.blueprint.model.ImportedBlueprint

interface BlueprintService {

    suspend fun listBlueprints(): List<Blueprint>

    suspend fun getRaw(id: Long): RawBlueprint

    suspend fun getBlueprint(id: Long): Blueprint

    suspend fun importBlueprint(url: String): ImportedBlueprint

    suspend fun readBlueprintAssetContents(id: Long, path: String): Pair<VirtualFile, ByteArray>
}
