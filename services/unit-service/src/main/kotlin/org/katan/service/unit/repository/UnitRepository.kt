package org.katan.service.unit.repository

import org.katan.model.unit.KUnit
import org.katan.model.unit.auditlog.AuditLogEntry
import org.katan.service.unit.model.UnitUpdateOptions

internal interface UnitRepository {

    suspend fun listUnits(): List<UnitEntity>

    suspend fun findUnitById(id: Long): UnitEntity?

    suspend fun createUnit(unit: KUnit)

    suspend fun updateUnit(
        id: Long,
        options: UnitUpdateOptions
    )

    suspend fun findAuditLogs(unitId: Long): List<AuditLogEntry>?

    suspend fun createAuditLog(auditLogEntry: AuditLogEntry)
}
