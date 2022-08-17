package org.katan.model.unit.auditlog

import org.katan.model.account.Account

interface AuditLog {

    val entries: List<AuditLogEntry>

    val actors: List<Account>
}
