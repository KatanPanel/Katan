package org.katan.http.logic

import io.ktor.http.HttpStatusCode
import io.ktor.server.application.call
import io.ktor.server.resources.get
import io.ktor.server.response.respond
import io.ktor.server.routing.Route
import io.ktor.server.routing.Routing
import io.ktor.server.routing.post
import org.katan.http.locations.Servers

internal fun Routing.server() {
    find()
    create()
}

private fun Route.find() = get<Servers.Get> {
    call.respond(HttpStatusCode.BadRequest)
}

private fun Route.create() = post<Servers.Create> {
    call.respond(HttpStatusCode.BadRequest)
}