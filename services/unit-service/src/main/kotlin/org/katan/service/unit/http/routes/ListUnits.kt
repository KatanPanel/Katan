package org.katan.service.unit.http.routes

import io.ktor.server.resources.get
import io.ktor.server.routing.Route
import org.katan.http.response.respond
import org.katan.service.unit.UnitService
import org.katan.service.unit.http.UnitRoutes
import org.katan.service.unit.http.dto.UnitResponse
import org.koin.ktor.ext.inject

internal fun Route.listUnits() {
    val unitService by inject<UnitService>()

    get<UnitRoutes.All> {
        val units = unitService.getUnits()
        respond(units.map(::UnitResponse))
    }
}
