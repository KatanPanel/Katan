package org.katan.http.routes.server.routes

import io.ktor.http.HttpStatusCode.Companion.Conflict
import io.ktor.http.HttpStatusCode.Companion.Created
import io.ktor.server.application.call
import io.ktor.server.request.receive
import io.ktor.server.resources.post
import io.ktor.server.routing.Route
import org.katan.http.respondError
import org.katan.http.respond
import org.katan.service.server.UnitCreateOptions
import org.katan.service.server.UnitService
import org.katan.http.routes.server.UnitConflict
import org.katan.http.routes.server.UnitMissingCreateOptions
import org.katan.http.routes.server.locations.Servers
import org.katan.service.server.UnitConflictException
import org.koin.ktor.ext.inject

@kotlinx.serialization.Serializable
private data class CreateServerRequest(val name: String)

internal fun Route.createServer() {
    val unitService by inject<UnitService>()

    post<Servers> {
        val request = try {
            call.receive<CreateServerRequest>()
        } catch (e: Throwable) {
            respondError(UnitMissingCreateOptions, e)
        }

        val server = try {
            unitService.create(UnitCreateOptions(request.name))
        } catch (e: UnitConflictException) {
            respondError(UnitConflict, e, Conflict)
        }

        respond(server, Created)
    }
}