package org.katan.http.routes.server.routes

import io.ktor.server.resources.get
import io.ktor.server.routing.Route
import org.katan.http.respondError
import org.katan.http.respondOk
import org.katan.service.server.ServerService
import org.katan.http.routes.server.ServerNotFound
import org.katan.http.routes.server.locations.Servers
import org.koin.ktor.ext.inject

internal fun Route.findServer() {
    val serverService by inject<ServerService>()

    get<Servers.Get> { parameters ->
        val server = serverService.get(parameters.id)
            ?: respondError(ServerNotFound)

        respondOk(server)
    }
}