package org.katan.http

import io.ktor.server.application.Application
import io.ktor.server.application.install
import io.ktor.server.plugins.autohead.AutoHeadResponse
import io.ktor.server.plugins.callloging.CallLogging
import io.ktor.server.plugins.defaultheaders.DefaultHeaders
import io.ktor.server.resources.Resources
import io.ktor.server.routing.Routing

internal fun Application.installFeatures(httpServer: HttpServer) {
    install(Routing)
    install(Resources)
    install(DefaultHeaders)
    install(AutoHeadResponse)
    install(CallLogging)
}