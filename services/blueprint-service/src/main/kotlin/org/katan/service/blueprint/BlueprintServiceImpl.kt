package org.katan.service.blueprint

import io.ktor.client.HttpClient
import org.katan.model.blueprint.Blueprint
import org.katan.model.blueprint.BlueprintNotFoundException
import org.katan.model.blueprint.RawBlueprint
import org.katan.service.blueprint.model.BlueprintImpl
import org.katan.service.blueprint.provider.BlueprintResourceProviderRegistry
import org.katan.service.blueprint.provider.GithubBlueprintResourceProvider
import org.katan.service.blueprint.repository.BlueprintEntity
import org.katan.service.blueprint.repository.BlueprintRepository
import org.katan.service.id.IdService

const val GITHUB_PROVIDER = "github"

internal class BlueprintServiceImpl(
    private val idService: IdService,
    private val httpClient: HttpClient,
    private val repository: BlueprintRepository
) : BlueprintService {

    init {
        BlueprintResourceProviderRegistry.register(
            GITHUB_PROVIDER,
            GithubBlueprintResourceProvider(httpClient)
        )
    }

    override suspend fun listBlueprints(): List<Blueprint> {
        return repository.findAll().map { it.toModel() }
    }

    override suspend fun getBlueprint(id: Long): Blueprint {
        return repository.find(id)?.toModel() ?: throw BlueprintNotFoundException()
    }

    override suspend fun listProvided(): List<RawBlueprint> {
        TODO("Not yet implemented")
    }

    override suspend fun importBlueprint(url: String): RawBlueprint {
        TODO("Not yet implemented")
    }

    override suspend fun registerBlueprint(rawBlueprint: RawBlueprint) {

    }

    private fun BlueprintEntity.toModel(): Blueprint {
        return BlueprintImpl(
            id = getId(),
            name = name,
            image = image,
            createdAt = createdAt
        )
    }

}
