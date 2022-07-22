package org.katan.http.routes.unit.routes

import io.ktor.http.HttpStatusCode.Companion.Conflict
import io.ktor.http.HttpStatusCode.Companion.Created
import io.ktor.server.application.call
import io.ktor.server.request.receive
import io.ktor.server.resources.post
import io.ktor.server.routing.Route
import kotlinx.serialization.SerializationException
import org.katan.http.UnitConflict
import org.katan.http.UnitMissingCreateOptions
import org.katan.http.respond
import org.katan.http.respondError
import org.katan.http.routes.unit.UnitResource
import org.katan.http.routes.unit.dto.CreateUnitRequest
import org.katan.http.routes.unit.dto.CreateUnitResponse
import org.katan.http.routes.unit.dto.UnitResponse
import org.katan.service.server.UnitConflictException
import org.katan.service.server.UnitCreateOptions
import org.katan.service.server.UnitService
import org.koin.ktor.ext.inject

internal fun Route.createUnit() {
    val unitService by inject<UnitService>()

    post<UnitResource> {
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
                    displayName = request.displayName,
                    description = request.description,
                    dockerImage = request.dockerImage
                )
            )
        } catch (e: UnitConflictException) {
            respondError(UnitConflict, e, Conflict)
        }

        respond(
            CreateUnitResponse(
                dockerImage = request.dockerImage,
                unit = UnitResponse(unit)
            ), Created
        )
    }
}