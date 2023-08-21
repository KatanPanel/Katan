package org.katan.service.unit.http.dto

import kotlinx.datetime.Instant
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import org.katan.model.account.Account
import org.katan.model.unit.auditlog.AuditLog
import org.katan.model.unit.auditlog.AuditLogChange
import org.katan.model.unit.auditlog.AuditLogEntry
import org.katan.model.unit.auditlog.AuditLogEvent

@Serializable
internal data class AuditLogResponse(
    val entries: List<AuditLogEntryResponse>,
    val actors: List<AuditLogActorResponse>
) {

    constructor(auditLog: AuditLog) : this(
        entries = auditLog.entries.map(::AuditLogEntryResponse),
        actors = auditLog.actors.map(::AuditLogActorResponse)
    )
}

@Serializable
internal data class AuditLogActorResponse internal constructor(
    val id: String,
    val username: String
) {

    constructor(account: Account) : this(
        id = account.id.value.toString(),
        username = account.username
    )
}

@Serializable
internal data class AuditLogEntryResponse(
    val id: String,
    @SerialName("target-id") val targetId: String,
    @SerialName("actor-id") val actorId: String?,
    val event: AuditLogEvent,
    val reason: String?,
    val additionalData: String?,
    @SerialName("created-at") val createdAt: Instant,
    val changes: List<AuditLogEntryChangesResponse>
) {

    constructor(entry: AuditLogEntry) : this(
        id = entry.id.value.toString(),
        targetId = entry.targetId.value.toString(),
        actorId = entry.actorId?.value?.toString(),
        event = entry.event,
        reason = entry.reason,
        additionalData = entry.additionalData,
        createdAt = entry.createdAt,
        changes = entry.changes.map(::AuditLogEntryChangesResponse)
    )
}

@Serializable
internal data class AuditLogEntryChangesResponse(
    val key: String,
    @SerialName("old-value") val oldValue: String?,
    @SerialName("new-value") val newValue: String?
) {

    constructor(change: AuditLogChange) : this(
        key = change.key,
        oldValue = change.oldValue,
        newValue = change.newValue
    )
}
