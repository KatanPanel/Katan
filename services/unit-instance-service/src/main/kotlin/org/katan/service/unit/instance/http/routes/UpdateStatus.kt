package org.katan.service.unit.instance.http.routes

import io.ktor.http.HttpStatusCode
import io.ktor.server.application.call
import io.ktor.server.request.receive
import io.ktor.server.resources.post
import io.ktor.server.response.respond
import io.ktor.server.routing.Route
import jakarta.validation.Validator
import org.katan.http.response.HttpError
import org.katan.http.response.respondError
import org.katan.http.response.validateOrThrow
import org.katan.model.instance.InstanceUpdateCode
import org.katan.service.unit.instance.InstanceNotFoundException
import org.katan.service.unit.instance.UnitInstanceService
import org.katan.service.unit.instance.http.UnitInstanceRoutes
import org.katan.service.unit.instance.http.dto.UpdateStatusCodeRequest
import org.koin.ktor.ext.inject

internal fun Route.updateStatus() {
    val unitInstanceService by inject<UnitInstanceService>()
    val validator by inject<Validator>()

    post<UnitInstanceRoutes.UpdateStatus> { params ->
        validator.validateOrThrow(params)

        val request = call.receive<UpdateStatusCodeRequest>()
        val code = InstanceUpdateCode.getByCode(request.code)
            ?: respondError(HttpError.InvalidInstanceUpdateCode)

        val id = params.instanceId.toLong()
        val instance = try {
            unitInstanceService.getInstance(id)
        } catch (_: InstanceNotFoundException) {
            respondError(HttpError.UnknownInstance)
        }

        unitInstanceService.updateInternalStatus(instance, code)
        call.respond(HttpStatusCode.NoContent)
    }
}
