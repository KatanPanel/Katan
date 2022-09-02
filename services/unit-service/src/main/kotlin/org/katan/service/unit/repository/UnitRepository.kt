package org.katan.service.unit.repository

import org.katan.model.unit.KUnit
import org.katan.model.unit.auditlog.AuditLogEntry
import org.katan.service.unit.model.UnitUpdateOptions

public interface UnitRepository {

    public suspend fun listUnits(): List<UnitEntity>

    public suspend fun findUnitById(id: Long): UnitEntity?

    public suspend fun createUnit(unit: KUnit)

    public suspend fun updateUnit(
        id: Long,
        options: UnitUpdateOptions
    )

    public suspend fun findAuditLogs(unitId: Long): List<AuditLogEntry>?

    public suspend fun createAuditLog(auditLogEntry: AuditLogEntry)
}
