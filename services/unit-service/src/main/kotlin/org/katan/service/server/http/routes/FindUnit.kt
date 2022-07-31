package org.katan.service.server.http.routes

import io.ktor.server.resources.get
import io.ktor.server.routing.Route
import org.katan.http.respond
import org.katan.http.respondError
import org.katan.service.id.IdService
import org.katan.service.server.UnitService
import org.katan.service.server.http.InvalidUnitIdFormat
import org.katan.service.server.http.UnitNotFound
import org.katan.service.server.http.UnitRoutes
import org.katan.service.server.http.dto.UnitResponse
import org.koin.ktor.ext.inject

internal fun Route.findUnit() {
    val unitService by inject<UnitService>()
    val idService by inject<IdService>()

    get<UnitRoutes.FindById> { parameters ->
        val id = try {
            idService.parse(parameters.id)
        } catch (e: IllegalArgumentException) {
            respondError(InvalidUnitIdFormat)
        }

        val unit = unitService.getUnit(id)
            ?: respondError(UnitNotFound)

        respond(UnitResponse(unit))
    }
}
