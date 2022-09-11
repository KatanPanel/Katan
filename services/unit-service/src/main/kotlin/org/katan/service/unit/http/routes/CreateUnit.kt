package org.katan.service.unit.http.routes

import io.ktor.http.HttpStatusCode
import io.ktor.server.application.call
import io.ktor.server.request.receive
import io.ktor.server.resources.post
import io.ktor.server.routing.Route
import jakarta.validation.Validator
import org.katan.http.response.respond
import org.katan.http.response.validateOrThrow
import org.katan.service.auth.http.shared.AccountKey
import org.katan.service.unit.UnitService
import org.katan.service.unit.http.UnitRoutes
import org.katan.service.unit.http.dto.CreateUnitRequest
import org.katan.service.unit.http.dto.CreateUnitResponse
import org.katan.service.unit.http.dto.UnitResponse
import org.katan.service.unit.model.UnitCreateOptions
import org.koin.ktor.ext.inject

internal fun Route.createUnit() {
    val unitService by inject<UnitService>()
    val validator by inject<Validator>()

    post<UnitRoutes> {
        val request = call.receive<CreateUnitRequest>()
        validator.validateOrThrow(request)

        val unit = unitService.createUnit(
            UnitCreateOptions(
                name = request.name!!,
                blueprint = request.blueprint!!.toLong(),
                externalId = null,
                actorId = call.attributes.getOrNull(AccountKey)?.id,
                network = UnitCreateOptions.Network(
                    host = request.network?.host,
                    port = request.network?.port
                )
            )
        )

        respond(
            response = CreateUnitResponse(UnitResponse(unit)),
            status = HttpStatusCode.Created
        )
    }
}
