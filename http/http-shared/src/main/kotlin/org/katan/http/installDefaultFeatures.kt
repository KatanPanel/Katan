package org.katan.http

import io.ktor.serialization.jackson.jackson
import io.ktor.server.application.Application
import io.ktor.server.application.install
import io.ktor.server.plugins.autohead.AutoHeadResponse
import io.ktor.server.plugins.callloging.CallLogging
import io.ktor.server.plugins.contentnegotiation.ContentNegotiation
import io.ktor.server.plugins.defaultheaders.DefaultHeaders
import io.ktor.server.plugins.statuspages.StatusPages
import io.ktor.server.resources.Resources
import io.ktor.server.response.respond
import io.ktor.server.routing.Routing

fun Application.installDefaultFeatures() {
    install(Routing)
    install(Resources)
    install(DefaultHeaders)
    install(AutoHeadResponse)
    install(CallLogging)
    install(ContentNegotiation) {
        jackson()
    }
    install(StatusPages) {
        exception<KatanHttpException> { call, cause ->
            cause.printStackTrace()
            call.respond(cause.httpStatus, mapOf(
                "code" to cause.code,
                "message" to cause.message
            ))
        }

        exception<Throwable> { _, cause ->
            cause.printStackTrace()
        }
    }
}