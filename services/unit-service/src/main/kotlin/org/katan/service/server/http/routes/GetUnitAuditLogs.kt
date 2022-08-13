package org.katan.service.server.http.routes

import io.ktor.server.resources.get
import io.ktor.server.routing.Route
import jakarta.validation.Validator
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
    val validator by inject<Validator>()

    get<UnitRoutes.GetUnitAuditLogs> { parameters ->
        validator.validate(parameters)

        val auditLogs = try {
            unitService.getAuditLogs(parameters.unitId.toLong())
        } catch (_: UnitNotFoundException) {
            respondError(HttpError.UnknownUnit)
        }

        respond(AuditLogResponse(auditLogs))
    }
}
