package org.katan.service.server.repository

import kotlinx.coroutines.Dispatchers.IO
import org.jetbrains.exposed.sql.Database
import org.jetbrains.exposed.sql.SchemaUtils
import org.jetbrains.exposed.sql.transactions.experimental.newSuspendedTransaction
import org.jetbrains.exposed.sql.transactions.experimental.suspendedTransaction
import org.jetbrains.exposed.sql.transactions.transaction
import org.jetbrains.exposed.sql.update
import org.katan.model.internal.Wrapper
import org.katan.model.internal.ifNotEmpty
import org.katan.model.unit.KUnit
import org.katan.model.unit.auditlog.AuditLogEntry
import org.katan.service.server.model.AuditLogChangeImpl
import org.katan.service.server.model.AuditLogEntryImpl
import org.katan.service.server.model.UnitUpdateOptions
import org.katan.service.server.repository.entity.UnitAuditLogChangeEntity
import org.katan.service.server.repository.entity.UnitAuditLogChangesTable
import org.katan.service.server.repository.entity.UnitAuditLogEntriesTable
import org.katan.service.server.repository.entity.UnitAuditLogEntryEntity
import org.katan.service.server.repository.entity.UnitEntityImpl
import org.katan.service.server.repository.entity.UnitTable
import kotlin.properties.ReadWriteProperty

internal class UnitRepositoryImpl(
    private val database: Database
) : UnitRepository {

    init {
        transaction(db = database) {
            SchemaUtils.create(UnitTable, UnitAuditLogEntriesTable, UnitAuditLogChangesTable)
        }
    }

    override suspend fun listUnits(): List<UnitEntityImpl> {
        return newSuspendedTransaction(db = database) {
            UnitEntityImpl.all().notForUpdate().toList()
        }
    }

    override suspend fun findUnitById(id: Long): UnitEntityImpl? {
        return newSuspendedTransaction(db = database) {
            UnitEntityImpl.findById(id)
        }
    }

    override suspend fun createUnit(unit: KUnit) {
        return newSuspendedTransaction(db = database) {
            UnitEntityImpl.new(unit.id) {
                this.name = unit.name
                this.externalId = unit.externalId
                this.instanceId = unit.instanceId
                this.nodeId = unit.nodeId
                this.createdAt = unit.createdAt
                this.updatedAt = unit.updatedAt
                this.deletedAt = unit.deletedAt
                this.status = unit.status.value
            }
        }
    }

    override suspend fun updateUnit(
        id: Long,
        options: UnitUpdateOptions
    ) {
        return newSuspendedTransaction(db = database) {
            UnitTable.update({ UnitTable.id eq id }) { update ->
                options.instanceId.ifNotEmpty { update[instanceId] = it }
                options.status.ifNotEmpty { update[status] = it.value }
                options.name.ifNotEmpty { update[name] = it }
            }
        }
    }

    override suspend fun findAuditLogs(unitId: Long): List<AuditLogEntry>? {
        return newSuspendedTransaction(db = database) {
            val entity = UnitAuditLogEntryEntity.find {
                UnitAuditLogEntriesTable.targetId eq unitId
            }.notForUpdate()

            if (entity.empty()) {
                return@newSuspendedTransaction null
            }

            entity.map { entry ->
                AuditLogEntryImpl(
                    id = entry.id.value,
                    targetId = entry.targetId,
                    actorId = entry.actorId,
                    event = entry.event,
                    reason = entry.reason,
                    additionalData = entry.additionalData,
                    createdAt = entry.createdAt,
                    changes = entry.changes.map { change ->
                        AuditLogChangeImpl(
                            key = change.key,
                            oldValue = change.oldValue,
                            newValue = change.newValue
                        )
                    }
                )
            }
        }
    }

    override suspend fun createAuditLog(auditLogEntry: AuditLogEntry) {
        return newSuspendedTransaction(db = database) {
            val auditLogEntryEntity = UnitAuditLogEntryEntity.new(auditLogEntry.id) {
                this.targetId = auditLogEntry.targetId
                this.actorId = auditLogEntry.actorId
                this.event = auditLogEntry.event
                this.reason = auditLogEntry.reason
                this.additionalData = auditLogEntry.additionalData
                this.createdAt = auditLogEntry.createdAt
            }

            for (change in auditLogEntry.changes)
                suspendedTransaction(IO) {
                    UnitAuditLogChangeEntity.new {
                        this.key = change.key
                        this.oldValue = change.oldValue
                        this.newValue = change.newValue
                        this.entry = auditLogEntryEntity
                    }
                }
        }
    }
}
