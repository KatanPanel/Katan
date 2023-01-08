package org.katan.service.unit.http.routes

import io.ktor.http.HttpStatusCode
import io.ktor.server.application.call
import io.ktor.server.request.receive
import io.ktor.server.resources.patch
import io.ktor.server.routing.Route
import jakarta.validation.Validator
import org.katan.http.response.HttpError
import org.katan.http.response.respond
import org.katan.http.response.respondError
import org.katan.http.response.validateOrThrow
import org.katan.model.wrap
import org.katan.service.auth.http.shared.AccountKey
import org.katan.service.unit.UnitService
import org.katan.service.unit.http.UnitRoutes
import org.katan.service.unit.http.dto.ModifyUnitRequest
import org.katan.service.unit.http.dto.UnitResponse
import org.katan.service.unit.model.UnitUpdateOptions
import org.koin.ktor.ext.inject

internal fun Route.modifyUnit() {
    val unitService by inject<UnitService>()
    val validator by inject<Validator>()

    patch<UnitRoutes.ById> { parameters ->
        validator.validateOrThrow(parameters)

        val req = call.receive<ModifyUnitRequest>()
        if (req.isEmpty()) {
            respondError(HttpError.InvalidRequestBody, HttpStatusCode.NotAcceptable)
        }

        val unitId = parameters.unitId.toLong()
        val updateOptions = UnitUpdateOptions(
            name = req.name.wrap(),
            actorId = call.attributes.getOrNull(AccountKey)?.id.wrap()
        )

        val result = try {
            unitService.updateUnit(
                unitId,
                updateOptions
            )
        } catch (e: Throwable) {
            respondError(HttpError.UnknownUnit)
        }

        respond(UnitResponse(result))
    }
}
