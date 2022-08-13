package org.katan.service.server

import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import kotlinx.datetime.Clock
import org.apache.logging.log4j.LogManager
import org.apache.logging.log4j.Logger
import org.katan.config.KatanConfig
import org.katan.model.unit.KUnit
import org.katan.model.unit.auditlog.AuditLog
import org.katan.model.unit.auditlog.AuditLogEvents
import org.katan.service.id.IdService
import org.katan.service.server.model.AuditLogEntryImpl
import org.katan.service.server.model.UnitCreateOptions
import org.katan.service.server.model.UnitImpl
import org.katan.service.server.model.UnitUpdateOptions
import org.katan.service.server.repository.UnitRepository
import org.katan.service.unit.instance.UnitInstanceService

public class LocalUnitServiceImpl(
    private val config: KatanConfig,
    private val idService: IdService,
    private val unitInstanceService: UnitInstanceService,
    private val unitRepository: UnitRepository
) : UnitService {

    private companion object {
        private val logger: Logger = LogManager.getLogger(LocalUnitServiceImpl::class.java)
    }

    private val registered: MutableList<KUnit> = mutableListOf()
    private val mutex = Mutex()

    override suspend fun getUnits(): List<KUnit> {
        return unitRepository.listUnits()
    }

    override suspend fun getUnit(id: Long): KUnit {
        return mutex.withLock {
            registered.firstOrNull {
                it.id == id
            } ?: throw UnitNotFoundException()
        }
    }

    override suspend fun createUnit(options: UnitCreateOptions): KUnit = mutex.withLock {
        val currentInstant = Clock.System.now()
        val spec = unitInstanceService.fromSpec(mapOf("image" to options.dockerImage))

        logger.info("Options: $options")
        val instance = runCatching {
            unitInstanceService.createInstanceFor(spec)
        }.onFailure { error ->
            logger.error("Failed to create unit instance.", error)
        }.getOrThrow()

        logger.info("Created")
        val generatedId = idService.generate()

        val unit = UnitImpl(
            id = generatedId,
            externalId = options.externalId,
            nodeId = config.nodeId,
            name = options.name,
            createdAt = currentInstant,
            updatedAt = currentInstant,
            instanceId = instance.id
//            status = if (instance == null) UnitStatus.MissingInstance else UnitStatus.Created
        )
        unitRepository.createUnit(unit)
        unitRepository.createAuditLog(
            AuditLogEntryImpl(
                id = idService.generate(),
                targetId = generatedId,
                actorId = null, // TODO determine actor id
                event = AuditLogEvents.UnitCreate,
                reason = null,
                changes = emptyList(),
                additionalData = null,
                createdAt = currentInstant
            )
        )

        registered.add(unit)
        return unit
    }

    override suspend fun updateUnit(id: Long, options: UnitUpdateOptions): KUnit {
        return unitRepository.updateUnit(id, options) ?: throw UnitNotFoundException()
    }

    override suspend fun getAuditLogs(unitId: Long): AuditLog {
        return unitRepository.findAuditLogs(unitId) ?: throw UnitNotFoundException()
    }
}
