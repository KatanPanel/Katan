package org.katan.service.unit.http.routes

import io.ktor.server.resources.get
import io.ktor.server.routing.Route
import jakarta.validation.Validator
import org.katan.http.response.HttpError
import org.katan.http.response.respond
import org.katan.http.response.respondError
import org.katan.http.response.validateOrThrow
import org.katan.service.unit.UnitNotFoundException
import org.katan.service.unit.UnitService
import org.katan.service.unit.http.UnitRoutes
import org.katan.service.unit.http.dto.UnitResponse
import org.koin.ktor.ext.inject

internal fun Route.getUnit() {
    val unitService by inject<UnitService>()
    val validator by inject<Validator>()

    get<UnitRoutes.ById> { parameters ->
        validator.validateOrThrow(parameters)

        val unit = try {
            unitService.getUnit(parameters.unitId.toLong())
        } catch (_: UnitNotFoundException) {
            respondError(HttpError.UnknownUnit)
        }

        respond(UnitResponse(unit))
    }
}
