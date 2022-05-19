package org.katan.http.module.server.routes

import io.ktor.http.HttpStatusCode
import io.ktor.server.application.call
import io.ktor.server.response.respond
import io.ktor.server.routing.Route
import io.ktor.server.routing.post
import io.ktor.server.util.getOrFail
import org.katan.http.SERVER_CONFLICT
import org.katan.http.module.server.locations.Servers
import org.katan.http.throwHttpError
import org.katan.service.server.ServerConflictException
import org.katan.service.server.ServerCreateOptions
import org.katan.service.server.ServerService
import org.koin.ktor.ext.inject

internal fun Route.createServer() {
    val serverService by inject<ServerService>()

    post<Servers.Create> {
        val name = call.parameters.getOrFail("name")

        val server = try {
            serverService.create(ServerCreateOptions(name))
        } catch (e: ServerConflictException) {
            throwHttpError(SERVER_CONFLICT, HttpStatusCode.Conflict)
        }

        call.respond(HttpStatusCode.Created, mapOf(
            "response" to "success",
            "data" to server
        ))
    }
}