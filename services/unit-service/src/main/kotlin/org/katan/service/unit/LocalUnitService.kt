package org.katan.service.unit

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers.IO
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.withContext
import kotlinx.datetime.Clock
import kotlinx.datetime.Instant
import org.katan.KatanConfig
import org.katan.EventsDispatcher
import org.katan.model.Snowflake
import org.katan.model.instance.InstanceStatus
import org.katan.model.toSnowflake
import org.katan.model.unit.KUnit
import org.katan.model.unit.Unit
import org.katan.model.unit.UnitStatus
import org.katan.model.unit.auditlog.AuditLog
import org.katan.model.unit.auditlog.AuditLogChange
import org.katan.model.unit.auditlog.AuditLogEntry
import org.katan.model.unit.auditlog.AuditLogEvents
import org.katan.model.unwrap
import org.katan.service.account.AccountService
import org.katan.service.id.IdService
import org.katan.service.instance.InstanceService
import org.katan.service.instance.model.CreateInstanceOptions
import org.katan.service.unit.model.UnitCreateOptions
import org.katan.service.unit.model.UnitUpdateOptions
import org.katan.service.unit.repository.UnitEntity
import org.katan.service.unit.repository.UnitRepository

internal class LocalUnitService(
    private val config: KatanConfig,
    private val unitRepository: UnitRepository,
    private val idService: IdService,
    private val accountService: AccountService,
    private val instanceService: InstanceService,
    private val eventsDispatcher: EventsDispatcher
) : UnitService, CoroutineScope by CoroutineScope(SupervisorJob()) {

    override suspend fun getUnits(): List<KUnit> =
        unitRepository.listUnits().map { unit -> unit.toDomain() }

    override suspend fun getUnit(id: Long): KUnit =
        unitRepository.findUnitById(id)?.toDomain() ?: throw UnitNotFoundException()

    override suspend fun createUnit(options: UnitCreateOptions): KUnit {
        val generatedId = idService.generate()
        val instance = instanceService.createInstance(
            options.blueprintId,
            CreateInstanceOptions(
                image = options.image,
                host = options.network?.host,
                port = options.network?.port
            )
        )
        val status: UnitStatus = when (instance.status) {
            InstanceStatus.ImagePullFailed -> UnitStatus.MissingInstance
            InstanceStatus.ImagePullInProgress -> UnitStatus.CreatingInstance
            InstanceStatus.ImagePullNeeded -> UnitStatus.CreatingInstance
            else -> UnitStatus.Created
        }
        val createdAt = Clock.System.now()
        val unit = Unit(
            id = generatedId,
            externalId = options.externalId,
            nodeId = config.nodeId,
            name = options.name,
            createdAt = createdAt,
            updatedAt = createdAt,
            status = status,
            deletedAt = null,
            instanceId = instance.id
        )

        unitRepository.createUnit(unit)
        eventsDispatcher.dispatch(UnitCreatedEvent(unit.id, unit.name, unit.nodeId))
        registerUnitCreateAuditLog(generatedId, options.actorId, Clock.System.now())

        return unit
    }

    private suspend fun registerUnitCreateAuditLog(
        targetId: Snowflake,
        actorId: Snowflake?,
        instant: Instant
    ) = unitRepository.createAuditLog(
        AuditLogEntry(
            id = idService.generate(),
            targetId = targetId,
            actorId = actorId,
            event = AuditLogEvents.UnitCreate,
            reason = null,
            changes = emptyList(),
            additionalData = null,
            createdAt = instant
        )
    )

    private suspend fun updateUnit(id: Long, options: UnitUpdateOptions, audit: Boolean): KUnit {
        // TODO use single query to fetch and update
        val actualUnit = getUnit(id)
        unitRepository.updateUnit(id, options)

        val updatedUnit = getUnit(id)
        if (!audit) {
            return updatedUnit
        }

        val changes = buildAuditLogChangesBasedOnUpdate(actualUnit, updatedUnit, options)
        if (changes.isNotEmpty()) {
            withContext(IO) {
                unitRepository.createAuditLog(
                    AuditLogEntry(
                        id = idService.generate(),
                        targetId = actualUnit.id,
                        actorId = options.actorId.unwrap(),
                        event = AuditLogEvents.UnitUpdate,
                        reason = null,
                        changes = buildAuditLogChangesBasedOnUpdate(
                            actualUnit,
                            updatedUnit,
                            options
                        ),
                        additionalData = null,
                        createdAt = Clock.System.now()
                    )
                )
            }
        }

        return updatedUnit
    }

    override suspend fun updateUnit(id: Long, options: UnitUpdateOptions): KUnit {
        return updateUnit(id, options, true)
    }

    private fun buildAuditLogChangesBasedOnUpdate(
        actualUnit: KUnit,
        updatedUnit: KUnit,
        options: UnitUpdateOptions
    ): List<AuditLogChange> {
        val changes = mutableListOf<AuditLogChange>()
        options.name?.let {
            changes.add(
                AuditLogChange(
                    key = "name",
                    oldValue = actualUnit.name,
                    newValue = updatedUnit.name
                )
            )
        }

        return changes.toList()
    }

    override suspend fun getAuditLogs(unitId: Long): AuditLog {
        val entries = unitRepository.findAuditLogs(unitId) ?: throw UnitNotFoundException()
        val actors = entries.mapNotNull { it.actorId }.distinct().mapNotNull { actorId ->
            withContext(IO) {
                accountService.getAccount(actorId)
            }
        }

        return AuditLog(
            entries,
            actors
        )
    }

    private fun UnitEntity.toDomain(): KUnit = Unit(
        id = getId().toSnowflake(),
        externalId = externalId,
        instanceId = instanceId?.toSnowflake(),
        nodeId = nodeId,
        name = name,
        createdAt = createdAt,
        updatedAt = updatedAt,
        deletedAt = deletedAt,
        status = UnitStatus.getByValue(status)
    )
}
