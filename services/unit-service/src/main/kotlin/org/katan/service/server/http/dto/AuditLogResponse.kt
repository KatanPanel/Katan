package org.katan.service.server.http.dto

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import org.katan.model.unit.auditlog.AuditLog
import org.katan.model.unit.auditlog.AuditLogChange
import org.katan.model.unit.auditlog.AuditLogEntry
import org.katan.model.unit.auditlog.AuditLogEvent

@Serializable
internal data class AuditLogResponse(
    val entries: List<AuditLogEntryResponse>
) {

    constructor(auditLog: AuditLog) : this(auditLog.entries.map(::AuditLogEntryResponse))
}

@Serializable
internal data class AuditLogEntryResponse(
    val id: String,
    @SerialName("target_id") val targetId: Long,
    @SerialName("actor_id") val actorId: Long?,
    val event: AuditLogEvent,
    val reason: String?,
    val additionalData: String?,
    val changes: List<AuditLogEntryChangesResponse>
) {

    constructor(entry: AuditLogEntry) : this(
        id = entry.id.toString(),
        targetId = entry.targetId,
        actorId = entry.actorId,
        event = entry.event,
        reason = entry.reason,
        additionalData = entry.additionalData,
        changes = entry.changes.map(::AuditLogEntryChangesResponse)
    )
}

@Serializable
internal data class AuditLogEntryChangesResponse(
    val key: String,
    @SerialName("old_value") val oldValue: String?,
    @SerialName("new_value") val newValue: String?
) {

    constructor(change: AuditLogChange) : this(
        key = change.key,
        oldValue = change.oldValue,
        newValue = change.newValue
    )
}
