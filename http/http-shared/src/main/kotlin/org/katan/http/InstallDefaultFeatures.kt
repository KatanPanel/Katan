package org.katan.http

import io.ktor.http.HttpStatusCode
import io.ktor.serialization.kotlinx.json.json
import io.ktor.server.application.Application
import io.ktor.server.application.install
import io.ktor.server.plugins.autohead.AutoHeadResponse
import io.ktor.server.plugins.callloging.CallLogging
import io.ktor.server.plugins.contentnegotiation.ContentNegotiation
import io.ktor.server.plugins.cors.routing.CORS
import io.ktor.server.plugins.defaultheaders.DefaultHeaders
import io.ktor.server.plugins.statuspages.StatusPages
import io.ktor.server.resources.Resources
import io.ktor.server.response.respond
import io.ktor.server.routing.Routing
import kotlinx.serialization.json.Json
import org.slf4j.LoggerFactory
import org.slf4j.event.Level

fun Application.installDefaultFeatures() {
    install(Routing)
    install(Resources)
    install(DefaultHeaders)
    install(AutoHeadResponse)
    install(CallLogging) {
        level = Level.DEBUG
        logger = LoggerFactory.getLogger("Ktor")
    }
    install(ContentNegotiation) {
        json(
            Json {
                ignoreUnknownKeys = true

                @Suppress("OPT_IN_USAGE")
                explicitNulls = false
            }
        )
    }
    install(StatusPages) {
        exception<KatanHttpException> { call, httpError ->
            httpError.cause?.printStackTrace()
            call.respond(httpError.httpStatus, HttpError(httpError.code, httpError.message))
        }

        exception<Throwable> { call, cause ->
            cause.printStackTrace()
            call.respond(HttpStatusCode.InternalServerError)
        }
    }
    install(CORS) {
        allowCredentials = true
        allowNonSimpleContentTypes = true
        anyHost()
    }
}
