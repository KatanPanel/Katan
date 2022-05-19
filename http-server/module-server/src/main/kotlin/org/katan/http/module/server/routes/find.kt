package org.katan.http.module.server.routes

import io.ktor.http.HttpStatusCode
import io.ktor.server.application.call
import io.ktor.server.resources.get
import io.ktor.server.response.respond
import io.ktor.server.routing.Route
import org.katan.http.SERVER_NOT_FOUND
import org.katan.http.module.server.locations.Servers
import org.katan.http.throwHttpError
import org.katan.service.server.ServerService
import org.koin.ktor.ext.inject

internal fun Route.findServer() {
    val serverService by inject<ServerService>()

    get<Servers.Get> { parameters ->
        val server = serverService.get(parameters.id)
            ?: throwHttpError(SERVER_NOT_FOUND, HttpStatusCode.BadRequest)

        call.respond(HttpStatusCode.OK, server)
    }
}