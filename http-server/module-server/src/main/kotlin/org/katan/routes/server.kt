package org.katan.routes

import io.ktor.http.HttpStatusCode
import io.ktor.server.application.call
import io.ktor.server.resources.get
import io.ktor.server.response.respond
import io.ktor.server.routing.Route
import io.ktor.server.routing.post
import org.katan.locations.Servers

internal fun Route.find() = get<Servers.Get> {
    call.respond(HttpStatusCode.BadRequest)
}

internal fun Route.create() = post<Servers.Create> {
    call.respond(HttpStatusCode.BadRequest)
}