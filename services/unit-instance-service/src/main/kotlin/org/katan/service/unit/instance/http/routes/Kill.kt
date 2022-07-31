package org.katan.service.unit.instance.http.routes

import io.ktor.http.HttpStatusCode
import io.ktor.server.application.call
import io.ktor.server.resources.post
import io.ktor.server.response.respond
import io.ktor.server.routing.Route
import org.katan.service.unit.instance.UnitInstanceService
import org.katan.service.unit.instance.http.UnitInstanceRoutes
import org.koin.ktor.ext.inject

internal fun Route.kill() {
    val unitInstanceService by inject<UnitInstanceService>()

    post<UnitInstanceRoutes.Kill> { parameters ->
        val instance = unitInstanceService.getInstance(parameters.instanceId.toLong())!!

        unitInstanceService.killInstance(instance)
        call.respond(HttpStatusCode.NoContent)
    }
}
