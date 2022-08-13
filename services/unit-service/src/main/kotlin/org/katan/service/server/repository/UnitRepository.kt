package org.katan.service.server.repository

import org.katan.model.unit.KUnit
import org.katan.model.unit.auditlog.AuditLog
import org.katan.model.unit.auditlog.AuditLogEntry
import org.katan.service.server.model.UnitUpdateOptions

public interface UnitRepository {

    public suspend fun listUnits(): List<KUnit>

    public suspend fun findUnitById(id: Long): KUnit?

    public suspend fun createUnit(unit: KUnit)

    public suspend fun updateUnit(id: Long, update: UnitUpdateOptions): KUnit?

    public suspend fun findAuditLogs(unitId: Long): AuditLog?

    public suspend fun createAuditLog(auditLogEntry: AuditLogEntry)
}
