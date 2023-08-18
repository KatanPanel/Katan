package org.katan.service.blueprint

import kotlinx.serialization.decodeFromString
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import org.katan.model.blueprint.Blueprint
import org.katan.model.blueprint.BlueprintSpec
import org.katan.service.blueprint.model.BlueprintImpl
import org.katan.service.blueprint.model.BlueprintSpecImpl
import org.katan.service.blueprint.provider.BlueprintSpecProvider
import org.katan.service.blueprint.provider.BlueprintSpecSource
import org.katan.service.blueprint.provider.RemoteBlueprintSpecSource
import org.katan.service.blueprint.repository.BlueprintEntity
import org.katan.service.blueprint.repository.BlueprintRepository
import org.katan.service.fs.FSService
import org.katan.service.id.IdService

interface BlueprintService {

    suspend fun listBlueprints(): List<Blueprint>

    suspend fun getSpec(id: Long): BlueprintSpec

    suspend fun getBlueprint(id: Long): Blueprint

    suspend fun importBlueprint(source: BlueprintSpecSource): BlueprintSpec
}

suspend inline fun BlueprintService.importBlueprint(url: String) =
    importBlueprint(RemoteBlueprintSpecSource(url))

internal class BlueprintServiceImpl(
    private val idService: IdService,
    private val blueprintRepository: BlueprintRepository,
    private val blueprintSpecProvider: BlueprintSpecProvider,
    private val fsService: FSService
) : BlueprintService {

    companion object {
        private const val ROOT = "blueprints"
    }

    private val json: Json = Json {
        coerceInputValues = false
        prettyPrint = true
    }

    override suspend fun listBlueprints(): List<Blueprint> {
        return blueprintRepository.findAll().map(this::toModel)
    }

    override suspend fun getSpec(id: Long): BlueprintSpec {
        val contents = fsService.readFile(
            bucket = null,
            destination = ROOT,
            name = id.toString()
        )

        return json.decodeFromString<BlueprintSpecImpl>(contents.decodeToString())
    }

    override suspend fun getBlueprint(id: Long): Blueprint {
        return blueprintRepository.find(id)?.let(::toModel)
            ?: throw BlueprintNotFoundException()
    }

    override suspend fun importBlueprint(source: BlueprintSpecSource): BlueprintSpec {
        val spec = blueprintSpecProvider.provide(source)
        val id = idService.generate()

        fsService.uploadFile(
            bucket = null,
            destination = ROOT,
            name = id.toString(),
            contents = json.encodeToString(spec as BlueprintSpecImpl).encodeToByteArray()
        )

        return spec
    }

    private fun toModel(entity: BlueprintEntity): Blueprint = with(entity) {
        BlueprintImpl(
            id = getId(),
            name = name,
            version = version,
            imageId = imageId,
            createdAt = createdAt,
            updatedAt = updatedAt
        )
    }
}
