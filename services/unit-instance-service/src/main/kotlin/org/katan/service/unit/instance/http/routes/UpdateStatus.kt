package org.katan.service.unit.instance.http.routes

import io.ktor.http.HttpStatusCode
import io.ktor.server.application.call
import io.ktor.server.request.receive
import io.ktor.server.resources.post
import io.ktor.server.response.respond
import io.ktor.server.routing.Route
import org.katan.http.response.HttpError
import org.katan.http.response.respondError
import org.katan.model.instance.UnitInstanceUpdateStatusCode
import org.katan.service.unit.instance.InstanceNotFoundException
import org.katan.service.unit.instance.UnitInstanceService
import org.katan.service.unit.instance.http.UnitInstanceRoutes
import org.katan.service.unit.instance.http.dto.UpdateStatusCodeRequest
import org.koin.ktor.ext.inject

internal fun Route.updateStatus() {
    val unitInstanceService by inject<UnitInstanceService>()

    post<UnitInstanceRoutes.UpdateStatus> { params ->
        val request = call.receive<UpdateStatusCodeRequest>()
        val code = UnitInstanceUpdateStatusCode.getByCode(request.code)
            ?: respondError(HttpError.InvalidInstanceUpdateCode)

        val id = params.instanceId.toLong()
        val instance = try {
            unitInstanceService.getInstance(id)
        } catch (_: InstanceNotFoundException) {
            respondError(HttpError.UnknownInstance)
        }

        unitInstanceService.updateInstanceStatus(instance, code)
        call.respond(HttpStatusCode.NoContent)
    }
}
