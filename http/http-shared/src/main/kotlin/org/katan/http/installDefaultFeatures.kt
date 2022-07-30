package org.katan.http

import io.ktor.http.HttpStatusCode
import io.ktor.serialization.kotlinx.json.json
import io.ktor.server.application.Application
import io.ktor.server.application.install
import io.ktor.server.auth.Authentication
import io.ktor.server.auth.jwt.jwt
import io.ktor.server.plugins.autohead.AutoHeadResponse
import io.ktor.server.plugins.callloging.CallLogging
import io.ktor.server.plugins.contentnegotiation.ContentNegotiation
import io.ktor.server.plugins.defaultheaders.DefaultHeaders
import io.ktor.server.plugins.statuspages.StatusPages
import io.ktor.server.resources.Resources
import io.ktor.server.response.respond
import io.ktor.server.routing.Routing
import kotlinx.serialization.json.Json
import org.katan.http.auth.AccountPrincipal
import org.katan.service.auth.AuthService
import org.koin.ktor.ext.inject

fun Application.installDefaultFeatures() {
    install(Routing)
    install(Resources)
    install(DefaultHeaders)
    install(AutoHeadResponse)
    install(CallLogging)
    install(ContentNegotiation) {
        json(Json {
            ignoreUnknownKeys = true
            explicitNulls = false
        })
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

    installAuthentication()
}

private fun Application.installAuthentication() {
    val authService by inject<AuthService>()
    install(Authentication) {
        jwt {
            realm = "Katan"

            validate { credentials ->
                val account =
                    credentials.payload.getClaim(authService.getIdentifier()).asString()?.let {
                        authService.verify(it)
                    } ?: respondError(AccountNotFound)

                AccountPrincipal(account)
            }
        }
    }
}