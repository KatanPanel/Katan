package org.katan.service.server.http.routes

import io.ktor.http.HttpStatusCode
import io.ktor.server.application.call
import io.ktor.server.request.receive
import io.ktor.server.resources.post
import io.ktor.server.routing.Route
import org.katan.http.response.respond
import org.katan.service.server.UnitCreateOptions
import org.katan.service.server.UnitService
import org.katan.service.server.http.UnitRoutes
import org.katan.service.server.http.dto.CreateUnitRequest
import org.katan.service.server.http.dto.CreateUnitResponse
import org.katan.service.server.http.dto.UnitResponse
import org.koin.ktor.ext.inject

internal fun Route.createUnit() {
    val unitService by inject<UnitService>()

    post<UnitRoutes> {
        val request = call.receive<CreateUnitRequest>()
        val unit = unitService.createUnit(
            UnitCreateOptions(
                name = request.name,
                externalId = request.externalId,
                dockerImage = request.dockerImage
            )
        )

        respond(
            response = CreateUnitResponse(UnitResponse(unit)),
            status = HttpStatusCode.Created
        )
    }
}
