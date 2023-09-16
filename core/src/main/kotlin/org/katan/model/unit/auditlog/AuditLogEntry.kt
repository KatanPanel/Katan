package org.katan.model.unit.auditlog

import kotlinx.datetime.Instant
import kotlinx.serialization.Serializable
import org.katan.model.Snowflake

@Serializable
data class AuditLogEntry(
    val id: Snowflake,
    val targetId: Snowflake,
    val actorId: Snowflake?,
    val event: AuditLogEvent,
    val reason: String?,
    val changes: List<AuditLogChange>,
    val additionalData: String?,
    val createdAt: Instant,
)
