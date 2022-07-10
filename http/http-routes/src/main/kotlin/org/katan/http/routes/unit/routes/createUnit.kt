package org.katan.http.routes.unit.routes

import io.ktor.http.HttpStatusCode.Companion.Conflict
import io.ktor.http.HttpStatusCode.Companion.Created
import io.ktor.server.application.call
import io.ktor.server.request.receive
import io.ktor.server.resources.post
import io.ktor.server.routing.Route
import org.katan.http.UnitConflict
import org.katan.http.UnitMissingCreateOptions
import org.katan.http.respond
import org.katan.http.respondError
import org.katan.http.routes.unit.dto.CreateUnitRequest
import org.katan.http.routes.unit.locations.UnitRoutes
import org.katan.service.server.UnitConflictException
import org.katan.service.server.UnitCreateOptions
import org.katan.service.server.UnitService
import org.koin.ktor.ext.inject

internal fun Route.createUnit() {
    val unitService by inject<UnitService>()

    post<UnitRoutes> {
        val request = try {
            call.receive<CreateUnitRequest>()
        } catch (e: Throwable) {
            respondError(UnitMissingCreateOptions, e)
        }

        val instance = try {
            unitService.create(UnitCreateOptions(request.name))
        } catch (e: UnitConflictException) {
            respondError(UnitConflict, e, Conflict)
        }

        respond(instance, Created)
    }
}