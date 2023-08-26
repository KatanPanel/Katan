package org.katan.service.unit.http.routes

import io.ktor.http.HttpStatusCode
import io.ktor.server.application.call
import io.ktor.server.resources.post
import io.ktor.server.routing.Route
import jakarta.validation.Validator
import org.katan.http.response.receiveValidating
import org.katan.http.response.respond
import org.katan.model.toSnowflake
import org.katan.service.auth.http.shared.AccountKey
import org.katan.service.unit.UnitService
import org.katan.service.unit.http.UnitRoutes
import org.katan.service.unit.http.dto.CreateUnitRequest
import org.katan.service.unit.http.dto.UnitResponse
import org.katan.service.unit.model.UnitCreateOptions
import org.koin.ktor.ext.inject

internal fun Route.createUnit() {
    val unitService by inject<UnitService>()
    val validator by inject<Validator>()

    post<UnitRoutes> {
        val request = call.receiveValidating<CreateUnitRequest>(validator)
        val unit = unitService.createUnit(
            UnitCreateOptions(
                name = request.name,
                blueprintId = request.blueprint.toSnowflake(),
                externalId = null,
                actorId = call.attributes.getOrNull(AccountKey)?.id,
                image = request.image,
                options = request.options,
                network = request.network?.let { network ->
                    UnitCreateOptions.Network(
                        host = network.host,
                        port = network.port,
                    )
                }
            )
        )

        respond(
            response = UnitResponse(unit),
            status = HttpStatusCode.Created,
        )
    }
}
