package org.katan.model.unit.auditlog

interface AuditLog {

    val entries: List<AuditLogEntry>
}
