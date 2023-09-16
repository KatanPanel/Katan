package org.katan.model.unit.auditlog

import kotlinx.serialization.Serializable
import org.katan.model.account.Account

@Serializable
data class AuditLog(
    val entries: List<AuditLogEntry>,
    val actors: List<Account>,
)
