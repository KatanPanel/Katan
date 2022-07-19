package org.katan.http.routes.unit.routes

import io.ktor.server.resources.get
import io.ktor.server.routing.Route
import org.katan.http.InvalidUnitIdFormat
import org.katan.http.UnitNotFound
import org.katan.http.respond
import org.katan.http.respondError
import org.katan.http.routes.unit.locations.UnitRoutes
import org.katan.service.id.IdService
import org.katan.service.server.UnitService
import org.koin.ktor.ext.inject

internal fun Route.findUnit() {
    val unitService by inject<UnitService>()
    val idService by inject<IdService>()

    get<UnitRoutes.Get> { parameters ->
        val unitId = try {
            idService.parse(parameters.id)
        } catch (e: IllegalArgumentException) {
            respondError(InvalidUnitIdFormat)
        }

        val instance = unitService.getUnit(unitId) ?: respondError(UnitNotFound)
        respond(instance)
    }
}