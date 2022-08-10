package org.katan.service.unit.instance.http.routes

import io.ktor.http.HttpStatusCode
import io.ktor.server.application.call
import io.ktor.server.request.receiveOrNull
import io.ktor.server.resources.post
import io.ktor.server.response.respond
import io.ktor.server.routing.Route
import org.katan.http.respondError
import org.katan.model.unit.UnitInstanceUpdateStatusCode
import org.katan.service.unit.instance.UnitInstanceService
import org.katan.service.unit.instance.http.UnitInstanceNotFound
import org.katan.service.unit.instance.http.UnitInstanceRoutes
import org.katan.service.unit.instance.http.UnknownInstanceUpdateStatusCode
import org.katan.service.unit.instance.http.dto.UpdateStatusCodeRequest
import org.koin.ktor.ext.inject

internal fun Route.updateStatus() {
    val unitInstanceService by inject<UnitInstanceService>()

    post<UnitInstanceRoutes.UpdateStatus> { parameters ->
        val code = call.receiveOrNull<UpdateStatusCodeRequest>()?.let { req ->
            UnitInstanceUpdateStatusCode.getByCode(req.code)
        } ?: respondError(
            UnknownInstanceUpdateStatusCode
        )

        val instance = unitInstanceService.getInstance(parameters.instanceId.toLong())
            ?: respondError(UnitInstanceNotFound)

        unitInstanceService.updateInstanceStatus(instance, code)
        call.respond(HttpStatusCode.NoContent)
    }
}
