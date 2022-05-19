package org.katan.http

import io.ktor.server.application.Application
import io.ktor.server.routing.routing
import org.katan.http.logic.server

fun Application.createRouter() = routing {
    server()
}