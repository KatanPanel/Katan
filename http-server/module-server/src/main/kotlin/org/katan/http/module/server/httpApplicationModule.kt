package org.katan.http

import io.ktor.server.application.Application
import io.ktor.server.application.call
import io.ktor.server.response.respond
import io.ktor.server.routing.get
import io.ktor.server.routing.routing
import org.katan.http.module.server.routes.createServer
import org.katan.http.module.server.routes.findServer

@Suppress("FunctionName")
fun Application.ServerModule() {
    routing {
        get {
            call.respond("Show de bola :)")
        }

        findServer()
        createServer()
    }
}