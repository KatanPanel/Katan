package org.katan.service.server.http.routes

import io.ktor.http.HttpStatusCode.Companion.BadRequest
import io.ktor.http.HttpStatusCode.Companion.OK
import io.ktor.server.application.call
import io.ktor.server.resources.get
import io.ktor.server.response.respond
import io.ktor.server.routing.Route
import org.katan.http.HttpError.Companion.ServerNotFound
import org.katan.http.httpResponse
import org.katan.http.throwHttpException
import org.katan.service.server.ServerService
import org.katan.service.server.http.locations.Servers
import org.koin.ktor.ext.inject

internal fun Route.findServer() {
    val serverService by inject<ServerService>()

    get<Servers.Get> { parameters ->
        val server = serverService.get(parameters.id)
            ?: throwHttpException(ServerNotFound, BadRequest)

        call.respond(OK, httpResponse(server))
    }
}