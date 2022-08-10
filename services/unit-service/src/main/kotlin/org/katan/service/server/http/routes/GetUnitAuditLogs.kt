package org.katan.service.server.http.routes

import io.ktor.server.resources.get
import io.ktor.server.routing.Route
import org.katan.http.respond
import org.katan.http.respondError
import org.katan.service.id.IdService
import org.katan.service.server.UnitService
import org.katan.service.server.http.UnitNotFound
import org.katan.service.server.http.UnitRoutes
import org.katan.service.server.http.dto.AuditLogResponse
import org.koin.ktor.ext.inject

internal fun Route.getUnitAuditLogs() {
    val unitService by inject<UnitService>()

    get<UnitRoutes.GetUnitAuditLogs> { parameters ->
        val unit = unitService.getUnit(parameters.unitId.toLong()) ?: respondError(UnitNotFound)
        val auditLogs = unitService.getAuditLogs(unit.id)

        respond(AuditLogResponse(auditLogs))
    }
}
