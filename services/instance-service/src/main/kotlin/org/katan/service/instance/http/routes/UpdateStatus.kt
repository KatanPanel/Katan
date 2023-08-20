package org.katan.service.instance.http.routes

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
import org.katan.model.instance.InstanceNotFoundException
import org.katan.model.instance.InstanceUpdateCode
import org.katan.service.instance.InstanceService
import org.katan.service.instance.http.InstanceRoutes
import org.katan.service.instance.http.dto.UpdateStatusCodeRequest
import org.koin.ktor.ext.inject

internal fun Route.updateStatus() {
    val instanceService by inject<InstanceService>()
    val validator by inject<Validator>()

    post<InstanceRoutes.UpdateStatus> { params ->
        validator.validateOrThrow(params)

        val request = call.receive<UpdateStatusCodeRequest>()
        val code = InstanceUpdateCode.getByCode(request.code)
            ?: respondError(HttpError.InvalidInstanceUpdateCode)

        val instance = try {
            instanceService.getInstance(params.instanceId)
        } catch (_: InstanceNotFoundException) {
            respondError(HttpError.UnknownInstance)
        }

        instanceService.updateInstanceStatus(instance, code)
        call.respond(HttpStatusCode.NoContent)
    }
}
