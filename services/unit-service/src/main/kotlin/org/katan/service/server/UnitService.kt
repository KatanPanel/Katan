package org.katan.service.server

import org.katan.model.unit.KUnit
import org.katan.model.unit.auditlog.AuditLog
import org.katan.model.unit.auditlog.AuditLogEntry

public interface UnitService {

    public suspend fun getUnit(id: Long): KUnit?

    public suspend fun createUnit(options: UnitCreateOptions): KUnit

    public suspend fun getAuditLogs(unitId: Long): AuditLog

    public suspend fun addAuditLog(unitId: Long, auditLog: AuditLogEntry)
}
