package org.katan.service.unit.repository

import kotlinx.coroutines.Dispatchers.IO
import org.jetbrains.exposed.sql.Database
import org.jetbrains.exposed.sql.SchemaUtils
import org.jetbrains.exposed.sql.transactions.experimental.newSuspendedTransaction
import org.jetbrains.exposed.sql.transactions.transaction
import org.jetbrains.exposed.sql.update
import org.katan.model.ifNotEmpty
import org.katan.model.toSnowflake
import org.katan.model.unit.KUnit
import org.katan.model.unit.auditlog.AuditLogChange
import org.katan.model.unit.auditlog.AuditLogEntry
import org.katan.service.unit.model.UnitUpdateOptions
import org.katan.service.unit.repository.entity.UnitAuditLogChangeEntity
import org.katan.service.unit.repository.entity.UnitAuditLogChangesTable
import org.katan.service.unit.repository.entity.UnitAuditLogEntriesTable
import org.katan.service.unit.repository.entity.UnitAuditLogEntryEntity
import org.katan.service.unit.repository.entity.UnitEntityImpl
import org.katan.service.unit.repository.entity.UnitTable

internal class UnitRepositoryImpl(private val database: Database) : UnitRepository {

    init {
        transaction(db = database) {
            SchemaUtils.create(UnitTable, UnitAuditLogEntriesTable, UnitAuditLogChangesTable)
        }
    }

    override suspend fun listUnits(): List<UnitEntity> = newSuspendedTransaction(db = database) {
        UnitEntityImpl.all().notForUpdate().toList()
    }

    override suspend fun findUnitById(id: Long): UnitEntity? = newSuspendedTransaction(db = database) {
        UnitEntityImpl.findById(id)
    }

    override suspend fun createUnit(unit: KUnit) {
        newSuspendedTransaction(db = database) {
            UnitEntityImpl.new(unit.id.value) {
                this.name = unit.name
                this.externalId = unit.externalId
                this.instanceId = unit.instanceId?.value
                this.nodeId = unit.nodeId
                this.createdAt = unit.createdAt
                this.updatedAt = unit.updatedAt
                this.deletedAt = unit.deletedAt
                this.status = unit.status.value
            }
        }
    }

    override suspend fun updateUnit(id: Long, options: UnitUpdateOptions) {
        return newSuspendedTransaction(db = database) {
            UnitTable.update({ UnitTable.id eq id }) { update ->
                options.instanceId.ifNotEmpty { instanceId -> update[this.instanceId] = instanceId.value }
                options.status.ifNotEmpty { status -> update[this.status] = status.value }
                options.name.ifNotEmpty { name -> update[this.name] = name }
            }
        }
    }

    override suspend fun findAuditLogs(unitId: Long): List<AuditLogEntry>? = newSuspendedTransaction(db = database) {
        val entity = UnitAuditLogEntryEntity.find {
            UnitAuditLogEntriesTable.targetId eq unitId
        }.notForUpdate()

        if (entity.empty()) {
            return@newSuspendedTransaction null
        }

        entity.map { entry ->
            AuditLogEntry(
                id = entry.id.value.toSnowflake(),
                targetId = entry.targetId.toSnowflake(),
                actorId = entry.actorId?.toSnowflake(),
                event = entry.event,
                reason = entry.reason,
                additionalData = entry.additionalData,
                createdAt = entry.createdAt,
                changes = entry.changes.map { change ->
                    AuditLogChange(
                        key = change.key,
                        oldValue = change.oldValue,
                        newValue = change.newValue
                    )
                }
            )
        }
    }

    override suspend fun createAuditLog(auditLogEntry: AuditLogEntry) {
        newSuspendedTransaction(db = database) {
            val auditLogEntryEntity = UnitAuditLogEntryEntity.new(auditLogEntry.id.value) {
                this.targetId = auditLogEntry.targetId.value
                this.actorId = auditLogEntry.actorId?.value
                this.event = auditLogEntry.event
                this.reason = auditLogEntry.reason
                this.additionalData = auditLogEntry.additionalData
                this.createdAt = auditLogEntry.createdAt
            }

            for (change in auditLogEntry.changes)
                newSuspendedTransaction(IO) {
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
