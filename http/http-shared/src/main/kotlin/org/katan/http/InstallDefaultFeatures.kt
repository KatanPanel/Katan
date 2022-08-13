package org.katan.http

import io.ktor.http.HttpHeaders
import io.ktor.http.HttpMethod
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
import kotlinx.serialization.SerializationException
import kotlinx.serialization.json.Json
import org.katan.http.response.HttpError
import org.katan.http.response.ValidationException
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
        exception<HttpException> { call, exception ->
            try {
                exception.cause?.printStackTrace()
                call.respond(exception.status, HttpError(exception.code, exception.message.orEmpty()))
            } catch (e: Throwable) {
                println("PUTA Q PARIU IRMAO DEU ERRO AQ FI: $e")
            }
        }

        exception<ValidationException> { call, exception ->
            call.respond(HttpStatusCode.UnprocessableEntity, exception.data)
        }

        exception<SerializationException> { call, exception ->
            call.respond(
                HttpStatusCode.UnprocessableEntity,
                HttpError.Generic(exception.localizedMessage)
            )
        }

        exception<Throwable> { call, exception ->
            exception.printStackTrace()
            call.respond(
                HttpStatusCode.InternalServerError,
                HttpError.Generic("${exception::class.simpleName}: ${exception.localizedMessage}")
            )
        }
    }

    install(CORS) {
        allowCredentials = true
        allowNonSimpleContentTypes = true
        allowMethod(HttpMethod.Options)
        allowMethod(HttpMethod.Post)
        allowMethod(HttpMethod.Patch)
        allowMethod(HttpMethod.Put)
        allowXHttpMethodOverride()
        allowHeader(HttpHeaders.Authorization)
        anyHost()
    }
}
