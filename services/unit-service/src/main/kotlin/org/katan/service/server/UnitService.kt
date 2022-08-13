package org.katan.service.server

import org.katan.model.unit.KUnit
import org.katan.model.unit.auditlog.AuditLog
import org.katan.service.server.model.UnitCreateOptions
import org.katan.service.server.model.UnitUpdateOptions

public interface UnitService {

    public suspend fun getUnits(): List<KUnit>

    public suspend fun getUnit(id: Long): KUnit

    public suspend fun createUnit(options: UnitCreateOptions): KUnit

    public suspend fun updateUnit(id: Long, options: UnitUpdateOptions): KUnit

    public suspend fun getAuditLogs(unitId: Long): AuditLog
}
