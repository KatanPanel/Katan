package org.katan.http.module.server

import io.ktor.server.application.Application
import io.ktor.server.routing.routing
import org.katan.http.module.server.routes.createServer
import org.katan.http.module.server.routes.findServer

@Suppress("FunctionName")
fun Application.ServerModule() = routing {
    findServer()
    createServer()
}