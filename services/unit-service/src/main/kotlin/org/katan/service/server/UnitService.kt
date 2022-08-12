package org.katan.service.server

import org.katan.model.unit.KUnit
import org.katan.model.unit.auditlog.AuditLog

public interface UnitService {

    public suspend fun getUnits(): List<KUnit>

    @Throws(UnitNotFoundException::class)
    public suspend fun getUnit(id: Long): KUnit

    public suspend fun createUnit(options: UnitCreateOptions): KUnit

    @Throws(UnitNotFoundException::class)
    public suspend fun getAuditLogs(unitId: Long): AuditLog
}
