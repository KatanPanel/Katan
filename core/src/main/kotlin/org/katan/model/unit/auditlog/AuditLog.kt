package org.katan.model.unit.auditlog

import org.katan.model.account.Account

public interface AuditLog {

    public val entries: List<AuditLogEntry>

    public val actors: List<Account>
}
