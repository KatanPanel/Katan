package org.katan.service.unit

import kotlinx.atomicfu.atomic
import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.CoroutineName
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers.IO
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.supervisorScope
import kotlinx.coroutines.withContext
import kotlinx.datetime.Clock
import kotlinx.datetime.Instant
import org.apache.logging.log4j.LogManager
import org.apache.logging.log4j.Logger
import org.katan.config.KatanConfig
import org.katan.model.instance.InstanceStatus
import org.katan.model.instance.UnitInstance
import org.katan.model.unit.KUnit
import org.katan.model.unit.UnitStatus
import org.katan.model.unit.auditlog.AuditLog
import org.katan.model.unit.auditlog.AuditLogChange
import org.katan.model.unit.auditlog.AuditLogEvents
import org.katan.model.unwrap
import org.katan.model.wrap
import org.katan.service.account.AccountService
import org.katan.service.blueprint.BlueprintService
import org.katan.service.id.IdService
import org.katan.service.instance.InstanceService
import org.katan.service.unit.model.AuditLogChangeImpl
import org.katan.service.unit.model.AuditLogEntryImpl
import org.katan.service.unit.model.AuditLogImpl
import org.katan.service.unit.model.UnitCreateOptions
import org.katan.service.unit.model.UnitImpl
import org.katan.service.unit.model.UnitUpdateOptions
import org.katan.service.unit.repository.UnitEntity
import org.katan.service.unit.repository.UnitRepository
import kotlin.time.Duration.Companion.seconds

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
        return supervisorScope {
            val blueprint = blueprintService.getBlueprint(options.blueprint)
            val id = idService.generate()
            var instance: UnitInstance? = null
            var status: UnitStatus = UnitStatus.Ready
            var alreadyResumed by atomic(false)

            // launch in parent context to prevent instance creation job be cancelled when this
            // supervisorScope child coroutine completes successfully or gets cancelled
            val job = coroutineScope {
                launch(
                    this@LocalUnitServiceImpl.coroutineContext + CoroutineExceptionHandler { _, error ->
                        error.printStackTrace()
                        logger.error("An error occurred while creating instance", error)
                    }
                ) {
                    logger.info("Creating instance...")
                    instance = instanceService.createInstance(
                        blueprint = blueprint,
                        host = options.network.host,
                        port = options.network.port
                    )

                    logger.info("Trying to update unit ($alreadyResumed)...")

                    // if instance is not null it was created synchronously or asynchronous creation job
                    // completed before the max timeout so, we can assign now the status that'll be the
                    // final status, based on the newly created instance status
                    if (alreadyResumed) {
                        updateUnit(
                            id = id,
                            options = UnitUpdateOptions(
                                instanceId = instance!!.id.wrap(),
                                status = statusBasedOnInstance(instance!!.status).wrap()
                            ),
                            audit = false
                        )
                    }
                }
            }

            // waits up to 3 secs until the instance is created so that if the instance is created,
            // the value returned is the correct one since the instance creation task is independent
            val waitingJobCompletion = job.isActive && !job.isCompleted /* isAsync */
            if (waitingJobCompletion) {
                delay(3.seconds)
            }

            // mark as resumed to prevent unit be updated before registration
            alreadyResumed = true

            if (instance == null) {
                status = UnitStatus.CreatingInstance
            }

            val now = Clock.System.now()
            val impl = createImpl(
                id = id,
                externalId = options.externalId,
                name = options.name,
                instant = now,
                instanceId = instance?.id,
                status = status
            )
            unitRepository.createUnit(impl)
            launch(IO) {
                registerUnitCreateAuditLog(id, options.actorId, now)
            }

            impl
        }
    }

    private fun statusBasedOnInstance(status: InstanceStatus): UnitStatus {
        return when {
            status.isRuntimeStatus || status is InstanceStatus.ImagePullCompleted -> UnitStatus.Ready
            status.isInitialStatus -> UnitStatus.CreatingInstance
            else -> UnitStatus.Created
        }
    }

    private fun createImpl(
        id: Long,
        externalId: String?,
        name: String,
        instant: Instant,
        instanceId: Long?,
        status: UnitStatus
    ): KUnit {
        return UnitImpl(
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
    }

    private suspend fun registerUnitCreateAuditLog(
        targetId: Long,
        actorId: Long?,
        instant: Instant
    ) {
        unitRepository.createAuditLog(
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
    }

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
