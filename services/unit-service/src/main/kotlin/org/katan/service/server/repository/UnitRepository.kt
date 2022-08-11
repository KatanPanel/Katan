package org.katan.service.server.repository

import org.katan.model.unit.KUnit
import org.katan.model.unit.auditlog.AuditLog
import org.katan.model.unit.auditlog.AuditLogEntry

public interface UnitRepository {

    public suspend fun findUnitById(id: Long): KUnit?

    public suspend fun createUnit(unit: KUnit)

    public suspend fun findAuditLogs(unitId: Long): AuditLog?

    public suspend fun createAuditLog(auditLogEntry: AuditLogEntry)
}
