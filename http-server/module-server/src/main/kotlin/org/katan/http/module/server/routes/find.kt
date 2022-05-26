package org.katan.http.module.server.routes

import io.ktor.http.HttpStatusCode
import io.ktor.http.HttpStatusCode.Companion.BadRequest
import io.ktor.http.HttpStatusCode.Companion.OK
import io.ktor.server.application.call
import io.ktor.server.resources.get
import io.ktor.server.response.respond
import io.ktor.server.routing.Route
import org.katan.http.HttpError
import org.katan.http.HttpError.Companion.ServerNotFound
import org.katan.http.httpResponse
import org.katan.http.module.server.locations.Servers
import org.katan.http.throwHttpException
import org.katan.service.server.ServerService
import org.koin.ktor.ext.inject

internal fun Route.findServer() {
    val serverService by inject<ServerService>()

    get<Servers.Get> { parameters ->
        val server = serverService.get(parameters.id)
            ?: throwHttpException(ServerNotFound, BadRequest)

        call.respond(OK, httpResponse(server))
    }
}