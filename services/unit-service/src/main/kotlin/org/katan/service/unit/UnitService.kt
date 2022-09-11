package org.katan.service.unit

import org.katan.model.unit.KUnit
import org.katan.model.unit.auditlog.AuditLog
import org.katan.service.unit.model.UnitCreateOptions
import org.katan.service.unit.model.UnitUpdateOptions

interface UnitService {

    suspend fun getUnits(): List<KUnit>

    suspend fun getUnit(id: Long): KUnit

    suspend fun createUnit(options: UnitCreateOptions): KUnit

    suspend fun updateUnit(id: Long, options: UnitUpdateOptions): KUnit

    suspend fun getAuditLogs(unitId: Long): AuditLog
}
