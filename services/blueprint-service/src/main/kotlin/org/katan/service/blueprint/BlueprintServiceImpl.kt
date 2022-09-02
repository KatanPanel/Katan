package org.katan.service.blueprint

import org.apache.logging.log4j.LogManager
import org.apache.logging.log4j.Logger
import org.katan.model.blueprint.Blueprint
import org.katan.model.blueprint.RawBlueprint
import org.katan.service.id.IdService

internal class BlueprintServiceImpl(
    private val idService: IdService
) : BlueprintService {

    companion object {
        private val logger: Logger = LogManager.getLogger(BlueprintServiceImpl::class.java)
    }

    override suspend fun getBlueprint(id: Long): Blueprint? {
        TODO("Not yet implemented")
    }

    override suspend fun downloadBlueprint(source: String): RawBlueprint {
        logger.info("Download blueprint from $source...")
    }
}
