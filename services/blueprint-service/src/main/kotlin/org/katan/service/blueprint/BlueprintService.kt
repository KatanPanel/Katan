package org.katan.service.blueprint

import kotlinx.datetime.Clock
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.json.Json
import org.katan.model.Snowflake
import org.katan.model.blueprint.Blueprint
import org.katan.model.blueprint.BlueprintSpec
import org.katan.model.blueprint.BlueprintSpecImage
import org.katan.model.blueprint.ImportedBlueprint
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

    suspend fun importBlueprint(source: BlueprintSpecSource): ImportedBlueprint
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

    override suspend fun importBlueprint(source: BlueprintSpecSource): ImportedBlueprint {
        val spec = blueprintSpecProvider.provide(source)
        val id = Snowflake(idService.generate())

        // save generated blueprint spec locally
        fsService.uploadFile(
            bucket = null,
            destination = ROOT,
            name = id.value.toString(),
            // TODO save spec contents :)
            contents = byteArrayOf()
        )

        // register blueprint on database
        val currentInstant = Clock.System.now()

        // TODO add support to Ref & Multiple image types
        val image = (spec.build.image as BlueprintSpecImage.Identifier).id
        require(image != null)

        val blueprint = BlueprintImpl(
            id = id,
            name = spec.name,
            version = spec.version,
            imageId = image,
            createdAt = currentInstant
        )
        // TODO create blueprint
//        blueprintRepository.create(blueprint)

        return ImportedBlueprint(blueprint, spec)
    }

    private fun toModel(entity: BlueprintEntity): Blueprint = with(entity) {
        BlueprintImpl(
            id = Snowflake(getId()),
            name = name,
            version = version,
            imageId = imageId,
            createdAt = createdAt,
            updatedAt = updatedAt ?: createdAt
        )
    }
}
