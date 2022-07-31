package org.katan.service.unit.instance.http.routes

import io.ktor.http.HttpStatusCode
import io.ktor.server.application.call
import io.ktor.server.resources.post
import io.ktor.server.response.respond
import io.ktor.server.routing.Route
import org.katan.service.unit.instance.UnitInstanceService
import org.katan.service.unit.instance.http.UnitInstanceRoutes
import org.koin.ktor.ext.inject

internal fun Route.stop() {
    val unitInstanceService by inject<UnitInstanceService>()

    post<UnitInstanceRoutes.Stop> { parameters ->
        val instance = unitInstanceService.getInstance(parameters.instanceId.toLong())!!

        unitInstanceService.stopInstance(instance)
        call.respond(HttpStatusCode.NoContent)
    }
}
