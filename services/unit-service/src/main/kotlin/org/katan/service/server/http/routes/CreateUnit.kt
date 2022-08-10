package org.katan.service.server.http.routes

import io.ktor.http.HttpStatusCode
import io.ktor.server.application.call
import io.ktor.server.request.receive
import io.ktor.server.resources.post
import io.ktor.server.routing.Route
import kotlinx.serialization.SerializationException
import org.katan.http.respond
import org.katan.http.respondError
import org.katan.service.server.UnitConflictException
import org.katan.service.server.UnitCreateOptions
import org.katan.service.server.UnitService
import org.katan.service.server.http.UnitConflict
import org.katan.service.server.http.UnitMissingCreateOptions
import org.katan.service.server.http.UnitRoutes
import org.katan.service.server.http.dto.CreateUnitRequest
import org.katan.service.server.http.dto.CreateUnitResponse
import org.katan.service.server.http.dto.UnitResponse
import org.koin.ktor.ext.inject

internal fun Route.createUnit() {
    val unitService by inject<UnitService>()

    post<UnitRoutes> {
        val request = try {
            call.receive<CreateUnitRequest>()
        } catch (e: SerializationException) {
            respondError(UnitMissingCreateOptions, e)
        }

        val unit = try {
            unitService.createUnit(
                UnitCreateOptions(
                    name = request.name,
                    externalId = request.externalId,
                    dockerImage = request.dockerImage
                )
            )
        } catch (e: UnitConflictException) {
            respondError(UnitConflict, e, HttpStatusCode.Conflict)
        }

        respond(CreateUnitResponse(unit = UnitResponse(unit)), HttpStatusCode.Created)
    }
}
