package org.katan.service.auth.http

import com.auth0.jwt.interfaces.JWTVerifier
import io.ktor.http.HttpStatusCode
import io.ktor.server.application.Application
import io.ktor.server.application.ApplicationCallPipeline
import io.ktor.server.application.call
import io.ktor.server.application.install
import io.ktor.server.auth.Authentication
import io.ktor.server.auth.authenticate
import io.ktor.server.auth.jwt.jwt
import io.ktor.server.auth.principal
import io.ktor.server.response.respond
import io.ktor.server.routing.routing
import org.katan.http.HttpModule
import org.katan.http.response.HttpError
import org.katan.http.response.respondError
import org.katan.service.auth.AuthService
import org.katan.service.auth.http.routes.login
import org.katan.service.auth.http.routes.verify
import org.katan.service.auth.http.shared.AccountKey
import org.katan.service.auth.http.shared.AccountPrincipal
import org.koin.ktor.ext.inject

internal class AuthHttpModule : HttpModule() {

    override fun install(app: Application) {
        app.installAuthentication()
        app.routing {
            intercept(ApplicationCallPipeline.Call) {
                call.principal<AccountPrincipal>()?.account?.let {
                    call.attributes.put(AccountKey, it)
                }
            }

            login()
            authenticate {
                verify()
            }
        }
    }

    private fun Application.installAuthentication() {
        val authService by inject<AuthService>()
        val jwtVerifier by inject<JWTVerifier>()

        install(Authentication) {
            jwt {
                realm = "Katan"
                verifier(jwtVerifier)

                challenge { _, _ ->
                    call.respond(HttpStatusCode.Unauthorized, HttpError.InvalidAccessToken)
                }

                validate { credentials ->
                    authService.verify(credentials.subject)
                        ?.let(::AccountPrincipal)
                        ?: respondError(HttpError.UnknownAccount)
                }
            }
        }
    }
}
