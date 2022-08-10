package org.katan.service.server.model

import kotlinx.datetime.Instant
import org.katan.model.unit.auditlog.AuditLog
import org.katan.model.unit.auditlog.AuditLogChange
import org.katan.model.unit.auditlog.AuditLogEntry
import org.katan.model.unit.auditlog.AuditLogEvent

internal data class AuditLogImpl(
    override val entries: List<AuditLogEntry>
) : AuditLog

internal data class AuditLogEntryImpl(
    override val id: Long,
    override val targetId: Long,
    override val actorId: Long?,
    override val event: AuditLogEvent,
    override val reason: String?,
    override val changes: List<AuditLogChange>,
    override val additionalData: String?,
    override val createdAt: Instant
) : AuditLogEntry

internal data class AuditLogChangeImpl(
    override val key: String,
    override val oldValue: String?,
    override val newValue: String?
) : AuditLogChange