package org.katan.service.blueprint

import com.typesafe.config.ConfigFactory
import com.typesafe.config.ConfigParseOptions
import com.typesafe.config.ConfigSyntax
import io.ktor.client.HttpClient
import io.ktor.client.request.get
import io.ktor.client.statement.bodyAsText
import kotlinx.coroutines.runBlocking
import kotlinx.serialization.hocon.Hocon
import kotlinx.serialization.hocon.decodeFromConfig
import org.apache.logging.log4j.LogManager
import org.apache.logging.log4j.Logger
import org.katan.model.blueprint.Blueprint
import org.katan.model.blueprint.BlueprintNotFoundException
import org.katan.model.blueprint.RawBlueprint
import org.katan.service.blueprint.model.BlueprintImpl
import org.katan.service.blueprint.model.RawBlueprintImpl
import org.katan.service.blueprint.repository.BlueprintEntity
import org.katan.service.blueprint.repository.BlueprintRepository
import org.katan.service.id.IdService

internal class BlueprintServiceImpl(
    private val idService: IdService,
    private val httpClient: HttpClient,
    private val repository: BlueprintRepository
) : BlueprintService {

    companion object {
        private val logger: Logger = LogManager.getLogger(BlueprintServiceImpl::class.java)
    }

    private val hocon = Hocon {
        useConfigNamingConvention = true
    }

    init {
        runBlocking {
            downloadBlueprint("https://raw.githubusercontent.com/KatanPanel/blueprints/main/blueprints/services/postgres/blueprint.conf")
        }
    }

    override suspend fun getBlueprint(id: Long): Blueprint {
        return repository.find(id)?.toModel() ?: throw BlueprintNotFoundException()
    }

    // TODO test it
    override suspend fun downloadBlueprint(source: String): RawBlueprint {
        logger.info("Download blueprint from $source...")

        val contents = httpClient.get(source)

        val body = contents.bodyAsText()
        val conf = ConfigFactory.parseString(
            body,
            ConfigParseOptions.defaults().setSyntax(ConfigSyntax.CONF)
        )

        val type = hocon.decodeFromConfig<RawBlueprintImpl>(conf)

        logger.info("Body")
        logger.info(type)
        return type
    }

    private fun BlueprintEntity.toModel(): Blueprint {
        return BlueprintImpl(
            id = getId(),
            name = name,
            image = image
        )
    }

}
