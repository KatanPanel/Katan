package org.katan.http.routes.server.routes

import io.ktor.server.resources.get
import io.ktor.server.routing.Route
import org.katan.http.respond
import org.katan.service.server.UnitService
import org.katan.http.routes.server.locations.Servers
import org.koin.ktor.ext.inject

internal fun Route.listServers() {
    val unitService by inject<UnitService>()

    get<Servers> { parameters ->
        respond(unitService.list())
    }
}