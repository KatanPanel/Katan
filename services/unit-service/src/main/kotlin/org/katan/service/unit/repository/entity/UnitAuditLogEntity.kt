package org.katan.service.unit.repository.entity

import org.jetbrains.exposed.dao.IntEntity
import org.jetbrains.exposed.dao.IntEntityClass
import org.jetbrains.exposed.dao.LongEntity
import org.jetbrains.exposed.dao.LongEntityClass
import org.jetbrains.exposed.dao.id.EntityID
import org.jetbrains.exposed.dao.id.IntIdTable
import org.jetbrains.exposed.dao.id.LongIdTable
import org.jetbrains.exposed.sql.kotlin.datetime.timestamp

internal object UnitAuditLogEntriesTable : LongIdTable("units_auditlogs_entries") {

    val targetId = long("target_id")
    val actorId = long("actor_id").nullable()
    val event = uinteger("event")
    val reason = varchar("reason", length = 255).nullable()
    val createdAt = timestamp("created_at")
    val additionalData = varchar("adt_data", length = 255).nullable()
}

internal class UnitAuditLogEntryEntity(id: EntityID<Long>) : LongEntity(id) {
    companion object : LongEntityClass<UnitAuditLogEntryEntity>(UnitAuditLogEntriesTable)

    var targetId by UnitAuditLogEntriesTable.targetId
    var actorId by UnitAuditLogEntriesTable.actorId
    var event by UnitAuditLogEntriesTable.event
    var reason by UnitAuditLogEntriesTable.reason
    var createdAt by UnitAuditLogEntriesTable.createdAt
    var additionalData by UnitAuditLogEntriesTable.additionalData
    val changes by UnitAuditLogChangeEntity referrersOn UnitAuditLogChangesTable.entryId
}

internal object UnitAuditLogChangesTable : IntIdTable("units_auditlogs_changes") {

    val entryId = reference("entry_id", UnitAuditLogEntriesTable)
    val key = varchar("key", length = 255)
    val oldValue = varchar("old_value", length = 255).nullable()
    val newValue = varchar("new_value", length = 255).nullable()
}

internal class UnitAuditLogChangeEntity(id: EntityID<Int>) : IntEntity(id) {
    companion object : IntEntityClass<UnitAuditLogChangeEntity>(UnitAuditLogChangesTable)

    var key by UnitAuditLogChangesTable.key
    var oldValue by UnitAuditLogChangesTable.oldValue
    var newValue by UnitAuditLogChangesTable.newValue
    var entry by UnitAuditLogEntryEntity referencedOn UnitAuditLogChangesTable.entryId
}
