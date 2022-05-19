package org.katan

import io.ktor.server.application.Application
import io.ktor.server.engine.ApplicationEngineEnvironmentBuilder
import io.ktor.server.routing.routing
import org.katan.routes.create
import org.katan.routes.find

@Suppress("FunctionName")
fun ApplicationEngineEnvironmentBuilder.ServerModule() = module {
    ServerModule()
}

@Suppress("FunctionName")
fun Application.ServerModule() {
    routing {
        find()
        create()
    }
}