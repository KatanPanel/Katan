package org.katan.service.server.http.routes

import io.ktor.server.resources.get
import io.ktor.server.routing.Route
import org.katan.http.response.HttpError
import org.katan.http.response.respond
import org.katan.http.response.respondError
import org.katan.service.server.UnitNotFoundException
import org.katan.service.server.UnitService
import org.katan.service.server.http.UnitRoutes
import org.katan.service.server.http.dto.AuditLogResponse
import org.koin.ktor.ext.inject

internal fun Route.getUnitAuditLogs() {
    val unitService by inject<UnitService>()

    get<UnitRoutes.GetUnitAuditLogs> { parameters ->
        val id = parameters.unitId.toLong()
        val auditLogs = try {
            unitService.getAuditLogs(id)
        } catch (_: UnitNotFoundException) {
            respondError(HttpError.UnknownUnit)
        }

        respond(AuditLogResponse(auditLogs))
    }
}
