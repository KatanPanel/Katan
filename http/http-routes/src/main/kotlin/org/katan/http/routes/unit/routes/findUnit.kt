package org.katan.http.routes.unit.routes

import io.ktor.server.resources.get
import io.ktor.server.routing.Route
import org.katan.http.UnitNotFound
import org.katan.http.respond
import org.katan.http.respondError
import org.katan.http.routes.unit.locations.UnitRoutes
import org.katan.service.server.UnitService
import org.koin.ktor.ext.inject

internal fun Route.findUnit() {
    val unitService by inject<UnitService>()

    get<UnitRoutes.Get> { parameters ->
        // TODO handle "toLong" properly
        val instance = unitService.getUnit(parameters.id.toLong())
            ?: respondError(UnitNotFound)

        respond(instance)
    }
}