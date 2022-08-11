package org.katan.service.server.repository

import kotlinx.coroutines.Dispatchers.IO
import org.jetbrains.exposed.sql.Database
import org.jetbrains.exposed.sql.SchemaUtils
import org.jetbrains.exposed.sql.selectBatched
import org.jetbrains.exposed.sql.transactions.experimental.newSuspendedTransaction
import org.jetbrains.exposed.sql.transactions.experimental.suspendedTransaction
import org.jetbrains.exposed.sql.transactions.transaction
import org.katan.model.unit.KUnit
import org.katan.model.unit.auditlog.AuditLog
import org.katan.model.unit.auditlog.AuditLogEntry
import org.katan.service.server.model.AuditLogEntryImpl
import org.katan.service.server.model.AuditLogImpl
import org.katan.service.server.model.UnitImpl
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

    override suspend fun findUnitById(id: Long): KUnit? {
        return newSuspendedTransaction(db = database) {
            UnitEntity.findById(id)?.let { entity ->
                UnitImpl(
                    id,
                    entity.externalId,
                    entity.instanceId,
                    entity.nodeId,
                    entity.name,
                    entity.createdAt,
                    entity.updatedAt,
                    entity.deletedAt
                )
            }
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

    override suspend fun findAuditLogs(unitId: Long): AuditLog? {
        return newSuspendedTransaction(db = database) {
            val query = UnitAuditLogEntriesTable.selectBatched {
                UnitAuditLogEntriesTable.targetId eq unitId
            }.singleOrNull() ?: return@newSuspendedTransaction null

            AuditLogImpl(
                query.map {
                    AuditLogEntryImpl(
                        id = it[UnitAuditLogEntriesTable.id].value,
                        targetId = it[UnitAuditLogEntriesTable.targetId],
                        actorId = it[UnitAuditLogEntriesTable.actorId],
                        event = it[UnitAuditLogEntriesTable.event],
                        reason = it[UnitAuditLogEntriesTable.reason],
                        changes = emptyList(),
                        additionalData = it[UnitAuditLogEntriesTable.additionalData],
                        createdAt = it[UnitAuditLogEntriesTable.createdAt]
                    )
                }
            )
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
