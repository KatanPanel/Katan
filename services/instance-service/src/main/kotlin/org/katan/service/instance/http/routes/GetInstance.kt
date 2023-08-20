package org.katan.service.instance.http.routes

import io.ktor.server.resources.get
import io.ktor.server.routing.Route
import jakarta.validation.Validator
import org.katan.http.response.HttpError
import org.katan.http.response.respond
import org.katan.http.response.respondError
import org.katan.http.response.validateOrThrow
import org.katan.model.instance.InstanceNotFoundException
import org.katan.service.instance.InstanceService
import org.katan.service.instance.http.InstanceRoutes
import org.katan.service.instance.http.dto.InstanceResponse
import org.koin.ktor.ext.inject

internal fun Route.getInstance() {
    val instanceService by inject<InstanceService>()
    val validator by inject<Validator>()

    get<InstanceRoutes.ById> { parameters ->
        validator.validateOrThrow(parameters)

        val instance = try {
            instanceService.getInstance(parameters.instanceId)
        } catch (_: InstanceNotFoundException) {
            respondError(HttpError.UnknownInstance)
        }

        respond(InstanceResponse(instance))
    }
}
