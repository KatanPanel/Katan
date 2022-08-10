package org.katan.service.server

import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import kotlinx.datetime.Clock
import org.apache.logging.log4j.LogManager
import org.apache.logging.log4j.Logger
import org.katan.config.KatanConfig
import org.katan.model.unit.KUnit
import org.katan.model.unit.auditlog.AuditLog
import org.katan.model.unit.auditlog.AuditLogEntry
import org.katan.model.unit.auditlog.AuditLogEvents
import org.katan.service.id.IdService
import org.katan.service.server.model.AuditLogEntryImpl
import org.katan.service.server.model.UnitImpl
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

    override suspend fun getUnit(id: Long): KUnit? = mutex.withLock {
        registered.firstOrNull {
            it.id == id
        }
    }

    override suspend fun createUnit(options: UnitCreateOptions): KUnit = mutex.withLock {
        if (registered.any { it.name.equals(options.name, ignoreCase = true) }) {
            throw UnitConflictException()
        }

        val unit = createUnit0(options)

        registered.add(unit)
        return unit
    }

    override suspend fun getAuditLogs(unitId: Long): AuditLog {
        return unitRepository.findAuditLogs(unitId)
    }

    override suspend fun addAuditLog(unitId: Long, auditLog: AuditLogEntry) {

    }

    private suspend fun createUnit0(options: UnitCreateOptions): KUnit {
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

        val impl = UnitImpl(
            id = generatedId,
            externalId = options.externalId,
            nodeId = config.nodeId,
            name = options.name,
            createdAt = currentInstant,
            updatedAt = currentInstant,
            instanceId = instance.id,
//            status = if (instance == null) UnitStatus.MissingInstance else UnitStatus.Created
        )
        unitRepository.createUnit(impl)
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

        return impl
    }
}
