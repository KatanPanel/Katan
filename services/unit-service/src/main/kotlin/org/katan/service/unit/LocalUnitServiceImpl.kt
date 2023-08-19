package org.katan.service.unit

import kotlinx.coroutines.CoroutineName
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers.IO
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.withContext
import kotlinx.datetime.Clock
import kotlinx.datetime.Instant
import org.apache.logging.log4j.LogManager
import org.apache.logging.log4j.Logger
import org.katan.config.KatanConfig
import org.katan.model.instance.InstanceStatus
import org.katan.model.unit.KUnit
import org.katan.model.unit.UnitStatus
import org.katan.model.unit.auditlog.AuditLog
import org.katan.model.unit.auditlog.AuditLogChange
import org.katan.model.unit.auditlog.AuditLogEvents
import org.katan.model.unwrap
import org.katan.service.account.AccountService
import org.katan.service.blueprint.BlueprintService
import org.katan.service.id.IdService
import org.katan.service.instance.InstanceService
import org.katan.service.instance.model.CreateInstanceOptions
import org.katan.service.unit.model.AuditLogChangeImpl
import org.katan.service.unit.model.AuditLogEntryImpl
import org.katan.service.unit.model.AuditLogImpl
import org.katan.service.unit.model.UnitCreateOptions
import org.katan.service.unit.model.UnitImpl
import org.katan.service.unit.model.UnitUpdateOptions
import org.katan.service.unit.repository.UnitEntity
import org.katan.service.unit.repository.UnitRepository

internal class LocalUnitServiceImpl(
    private val config: KatanConfig,
    private val unitRepository: UnitRepository,
    private val idService: IdService,
    private val accountService: AccountService,
    private val instanceService: InstanceService,
    private val blueprintService: BlueprintService
) : UnitService,
    CoroutineScope by CoroutineScope(SupervisorJob() + CoroutineName(LocalUnitServiceImpl::class.simpleName!!)) {

    private companion object {
        private val logger: Logger = LogManager.getLogger(LocalUnitServiceImpl::class.java)
    }

    override suspend fun getUnits(): List<KUnit> {
        return unitRepository.listUnits().map { it.toDomain() }
    }

    override suspend fun getUnit(id: Long): KUnit {
        return unitRepository.findUnitById(id)?.toDomain() ?: throw UnitNotFoundException()
    }

    override suspend fun createUnit(options: UnitCreateOptions): KUnit {
        val id = idService.generate()
        val instance = instanceService.createInstance(
            blueprintId = options.blueprintId,
            options = CreateInstanceOptions(
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
        val now = Clock.System.now()
        val impl = createImpl(
            id = id,
            externalId = options.externalId,
            name = options.name,
            instant = now,
            instanceId = instance.id,
            status = status
        )

        unitRepository.createUnit(impl)
        registerUnitCreateAuditLog(id, options.actorId, now)

        return impl
    }

    private fun createImpl(
        id: Long,
        externalId: String?,
        name: String,
        instant: Instant,
        instanceId: Long?,
        status: UnitStatus
    ): KUnit = UnitImpl(
        id = id,
        externalId = externalId,
        nodeId = config.nodeId,
        name = name,
        createdAt = instant,
        updatedAt = instant,
        status = status,
        deletedAt = null,
        instanceId = instanceId
    )

    private suspend fun registerUnitCreateAuditLog(
        targetId: Long,
        actorId: Long?,
        instant: Instant
    ) = unitRepository.createAuditLog(
        AuditLogEntryImpl(
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
                    AuditLogEntryImpl(
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
                AuditLogChangeImpl(
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

        return AuditLogImpl(
            entries,
            actors
        )
    }

    private fun UnitEntity.toDomain(): KUnit {
        return UnitImpl(
            id = getId(),
            externalId = externalId,
            instanceId = instanceId,
            nodeId = nodeId,
            name = name,
            createdAt = createdAt,
            updatedAt = updatedAt,
            deletedAt = deletedAt,
            status = UnitStatus.getByValue(status)
        )
    }
}
