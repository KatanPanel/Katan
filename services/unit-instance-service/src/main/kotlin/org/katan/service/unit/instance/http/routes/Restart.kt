package org.katan.service.unit.instance.http.routes

import io.ktor.http.HttpStatusCode
import io.ktor.server.application.call
import io.ktor.server.resources.post
import io.ktor.server.response.respond
import io.ktor.server.routing.Route
import org.katan.service.unit.instance.UnitInstanceService
import org.katan.service.unit.instance.http.UnitInstanceRoutes
import org.koin.ktor.ext.inject

internal fun Route.restart() {
    val unitInstanceService by inject<UnitInstanceService>()

    post<UnitInstanceRoutes.Restart> { parameters ->
        val instance = unitInstanceService.getInstance(parameters.instanceId.toLong())!!

        unitInstanceService.restartInstance(instance)
        call.respond(HttpStatusCode.NoContent)
    }
}
