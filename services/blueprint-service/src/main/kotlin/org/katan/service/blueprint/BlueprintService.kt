package org.katan.service.blueprint

import kotlinx.datetime.Clock
import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.decodeFromStream
import org.katan.model.blueprint.Blueprint
import org.katan.model.blueprint.BlueprintSpec
import org.katan.service.blueprint.model.BlueprintImpl
import org.katan.service.blueprint.model.BlueprintSpecImpl
import org.katan.service.blueprint.provider.BlueprintSpecProvider
import org.katan.service.blueprint.provider.BlueprintSpecSource
import org.katan.service.blueprint.provider.RemoteBlueprintSpecSource
import org.katan.service.blueprint.repository.BlueprintEntity
import org.katan.service.blueprint.repository.BlueprintRepository
import org.katan.service.id.IdService

interface BlueprintService {

    suspend fun listBlueprints(): List<Blueprint>

    suspend fun getBlueprint(id: Long): Blueprint

    suspend fun importBlueprint(source: BlueprintSpecSource): BlueprintSpec
}

suspend inline fun BlueprintService.importBlueprint(url: String) =
    importBlueprint(RemoteBlueprintSpecSource(url))

internal class BlueprintServiceImpl(
    private val idService: IdService,
    private val blueprintRepository: BlueprintRepository,
    private val blueprintSpecProvider: BlueprintSpecProvider
) : BlueprintService {

    private val json: Json = Json {
        coerceInputValues = false
        prettyPrint = true
    }

    override suspend fun listBlueprints(): List<Blueprint> =
        blueprintRepository.findAll().map(::toModel)

    override suspend fun getBlueprint(id: Long): Blueprint =
        blueprintRepository.find(id)?.let(::toModel)
            ?: throw BlueprintNotFoundException()

    override suspend fun importBlueprint(source: BlueprintSpecSource): BlueprintSpec {
        val spec = blueprintSpecProvider.provide(source)
        blueprintRepository.create(
            id = idService.generate(),
            spec = json.encodeToString(spec as BlueprintSpecImpl).encodeToByteArray(),
            createdAt = Clock.System.now()
        )

        return spec
    }

    @OptIn(ExperimentalSerializationApi::class)
    private fun toModel(entity: BlueprintEntity): Blueprint = BlueprintImpl(
        id = entity.getId(),
        createdAt = entity.createdAt,
        updatedAt = entity.updatedAt,
        spec = json.decodeFromStream<BlueprintSpecImpl>(entity.content.inputStream)
    )
}
