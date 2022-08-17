package org.katan.service.server.repository

import kotlinx.coroutines.Dispatchers.IO
import org.jetbrains.exposed.sql.Database
import org.jetbrains.exposed.sql.SchemaUtils
import org.jetbrains.exposed.sql.transactions.experimental.newSuspendedTransaction
import org.jetbrains.exposed.sql.transactions.experimental.suspendedTransaction
import org.jetbrains.exposed.sql.transactions.transaction
import org.katan.model.unit.KUnit
import org.katan.model.unit.auditlog.AuditLogEntry
import org.katan.service.server.model.AuditLogChangeImpl
import org.katan.service.server.model.AuditLogEntryImpl
import org.katan.service.server.model.UnitImpl
import org.katan.service.server.model.UnitUpdateOptions
import org.katan.service.server.repository.entity.UnitAuditLogChangeEntity
import org.katan.service.server.repository.entity.UnitAuditLogChangesTable
import org.katan.service.server.repository.entity.UnitAuditLogEntriesTable
import org.katan.service.server.repository.entity.UnitAuditLogEntryEntity
import org.katan.service.server.repository.entity.UnitEntity
import org.katan.service.server.repository.entity.UnitTable

internal class UnitRepositoryImpl(
    private val database: Database
) : UnitRepository {

    init {
        transaction(db = database) {
            SchemaUtils.create(UnitTable, UnitAuditLogEntriesTable, UnitAuditLogChangesTable)
        }
    }

    override suspend fun listUnits(): List<KUnit> {
        return newSuspendedTransaction(db = database) {
            UnitEntity.all().notForUpdate().map { entity -> entity.toDomain() }
        }
    }

    override suspend fun findUnitById(id: Long): KUnit? {
        return newSuspendedTransaction(db = database) {
            UnitEntity.findById(id)?.toDomain()
        }
    }

    override suspend fun createUnit(unit: KUnit) {
        return newSuspendedTransaction(db = database) {
            UnitEntity.new(unit.id) {
                this.name = unit.name
                this.externalId = unit.externalId
                this.instanceId = unit.instanceId
                this.nodeId = unit.nodeId
                this.createdAt = unit.createdAt
                this.updatedAt = unit.updatedAt
                this.deletedAt = unit.deletedAt
            }
        }
    }

    override suspend fun updateUnit(id: Long, update: UnitUpdateOptions): KUnit? {
        return newSuspendedTransaction(db = database) {
            val entity = UnitEntity.findById(id) ?: return@newSuspendedTransaction null
            update.name?.let { entity.name = it }

            entity.toDomain()
        }
    }

    override suspend fun findAuditLogs(unitId: Long): List<AuditLogEntry>? {
        return newSuspendedTransaction(db = database) {
            val entity = UnitAuditLogEntryEntity.find {
                UnitAuditLogEntriesTable.targetId eq unitId
            }.notForUpdate()

            if (entity.empty())
                return@newSuspendedTransaction null

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

    private fun UnitEntity.toDomain(): KUnit {
        return UnitImpl(
            id.value,
            externalId,
            instanceId,
            nodeId,
            name,
            createdAt,
            updatedAt,
            deletedAt
        )
    }
}
