package org.katan.service.server

import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import kotlinx.datetime.Clock
import org.apache.logging.log4j.LogManager
import org.apache.logging.log4j.Logger
import org.katan.config.KatanConfig
import org.katan.model.unit.KUnit
import org.katan.service.id.IdService
import org.katan.service.unit.instance.UnitInstanceService

public class LocalUnitServiceImpl(
    private val idService: IdService,
    private val unitInstanceService: UnitInstanceService,
    private val config: KatanConfig
) : UnitService {

    private companion object {
        private val logger: Logger = LogManager.getLogger(LocalUnitServiceImpl::class.java)
    }

    private val registered: MutableList<KUnit> = mutableListOf()
    private val mutex = Mutex()

    override suspend fun getUnit(id: Long): KUnit? = mutex.withLock {
        registered.firstOrNull {
            it.id == id
        }
    }

    override suspend fun createUnit(options: UnitCreateOptions): KUnit {
        mutex.withLock {
            if (registered.any { it.name.equals(options.name, ignoreCase = true) })
                throw UnitConflictException()
        }

        val unit = createUnit0(options)

        mutex.withLock {
            registered.add(unit)
        }

        return unit
    }

    private suspend fun createUnit0(options: UnitCreateOptions): KUnit {
        val currentInstant = Clock.System.now()

        // TODO no magic string
        val spec = unitInstanceService.fromSpec(mapOf(
            "kind" to "docker",
            "image" to options.dockerImage
        ))

        logger.info("Creating unit instance: $options (${unitInstanceService::class.simpleName})...")
        val instance = unitInstanceService.createInstanceFor(spec)

        logger.info("Generating unit unique identifier...")
        val unitId = idService.generate()

        return UnitImpl(
            id = unitId,
            externalId = options.externalId,
            nodeId = config.nodeId,
            name = options.name,
            displayName = options.displayName,
            description = options.description,
            createdAt = currentInstant,
            updatedAt = currentInstant,
            instance = instance
        ).also { logger.info("New unit created: $it") }
    }

}