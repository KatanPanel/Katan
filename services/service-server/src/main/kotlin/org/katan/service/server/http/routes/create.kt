package org.katan.service.server.http.routes

import io.ktor.http.HttpStatusCode.Companion.BadRequest
import io.ktor.http.HttpStatusCode.Companion.Conflict
import io.ktor.http.HttpStatusCode.Companion.Created
import io.ktor.server.application.call
import io.ktor.server.request.receive
import io.ktor.server.resources.post
import io.ktor.server.response.respond
import io.ktor.server.routing.Route
import org.katan.http.httpResponse
import org.katan.http.throwHttpException
import org.katan.service.server.ServerConflictException
import org.katan.service.server.ServerCreateOptions
import org.katan.service.server.ServerService
import org.katan.service.server.http.ServerConflict
import org.katan.service.server.http.ServerMissingCreateOptions
import org.katan.service.server.http.locations.Servers
import org.koin.ktor.ext.inject

@kotlinx.serialization.Serializable
private data class CreateServerRequest(val name: String)

internal fun Route.createServer() {
    val serverService by inject<ServerService>()

    post<Servers> {
        val request = runCatching {
            call.receive<CreateServerRequest>()
        }.recoverCatching { error ->
            throwHttpException(ServerMissingCreateOptions, BadRequest, error)
        }.getOrThrow()

        val server = try {
            serverService.create(ServerCreateOptions(request.name))
        } catch (e: ServerConflictException) {
            throwHttpException(ServerConflict, Conflict, e)
        }

        call.respond(Created, httpResponse(server))
    }
}