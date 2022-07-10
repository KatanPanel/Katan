package org.katan.http.routes.unit.routes

import io.ktor.server.resources.get
import io.ktor.server.routing.Route
import org.katan.http.respond
import org.katan.http.routes.unit.locations.UnitRoutes
import org.katan.service.server.UnitService
import org.koin.ktor.ext.inject

internal fun Route.listServers() {
    val unitService by inject<UnitService>()

    get<UnitRoutes> {
        respond(unitService.list())
    }
}