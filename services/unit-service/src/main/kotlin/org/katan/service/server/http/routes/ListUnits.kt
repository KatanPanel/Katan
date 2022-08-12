package org.katan.service.server.http.routes

import io.ktor.server.resources.get
import io.ktor.server.routing.Route
import org.katan.http.response.respond
import org.katan.service.server.UnitService
import org.katan.service.server.http.UnitRoutes
import org.katan.service.server.http.dto.ListUnitsResponse
import org.katan.service.server.http.dto.UnitResponse
import org.koin.ktor.ext.inject

internal fun Route.listUnits() {
    val unitService by inject<UnitService>()

    get<UnitRoutes.All> {
        val units = unitService.getUnits()

        respond(ListUnitsResponse(units.map(::UnitResponse)))
    }
}
