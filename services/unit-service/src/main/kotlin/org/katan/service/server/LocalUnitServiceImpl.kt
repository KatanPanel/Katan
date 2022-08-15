package org.katan.service.server

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import kotlinx.datetime.Clock
import org.apache.logging.log4j.LogManager
import org.apache.logging.log4j.Logger
import org.katan.config.KatanConfig
import org.katan.model.unit.KUnit
import org.katan.model.unit.auditlog.AuditLog
import org.katan.model.unit.auditlog.AuditLogEvents
import org.katan.service.id.IdService
import org.katan.service.server.model.AuditLogChangeImpl
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

    override suspend fun getUnits(): List<KUnit> {
        return unitRepository.listUnits()
    }

    override suspend fun getUnit(id: Long): KUnit {
        return unitRepository.findUnitById(id) ?: throw UnitNotFoundException()
    }

    override suspend fun createUnit(options: UnitCreateOptions): KUnit {
        val currentInstant = Clock.System.now()
        val instance = runCatching {
            unitInstanceService.createInstanceFor(options.dockerImage)
        }.onFailure { error ->
            logger.error("Failed to create unit instance.", error)
        }.getOrThrow()

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

        withContext(Dispatchers.IO) {
            unitRepository.createAuditLog(
                AuditLogEntryImpl(
                    id = idService.generate(),
                    targetId = generatedId,
                    actorId = options.actorId,
                    event = AuditLogEvents.UnitCreate,
                    reason = null,
                    changes = emptyList(),
                    additionalData = null,
                    createdAt = currentInstant
                )
            )
        }

        return unit
    }

    override suspend fun updateUnit(id: Long, options: UnitUpdateOptions): KUnit {
        val actualUnit = getUnit(id)
        val updatedUnit = unitRepository.updateUnit(id, options)
            ?: throw UnitNotFoundException() // possible de-synchronization guarantee

        withContext(Dispatchers.IO) {
            unitRepository.createAuditLog(
                AuditLogEntryImpl(
                    id = idService.generate(),
                    targetId = actualUnit.id,
                    actorId = options.actorId,
                    event = AuditLogEvents.UnitUpdate,
                    reason = null,
                    changes = listOf(
                        AuditLogChangeImpl(
                            key = "name",
                            oldValue = actualUnit.name,
                            newValue = updatedUnit.name
                        )
                    ),
                    additionalData = null,
                    createdAt = Clock.System.now()
                )
            )
        }

        return updatedUnit
    }

    override suspend fun getAuditLogs(unitId: Long): AuditLog {
        return unitRepository.findAuditLogs(unitId) ?: throw UnitNotFoundException()
    }
}
