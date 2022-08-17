package org.katan.service.auth.http

import com.auth0.jwt.interfaces.JWTVerifier
import io.ktor.http.HttpStatusCode
import io.ktor.server.application.Application
import io.ktor.server.application.ApplicationCall
import io.ktor.server.application.ApplicationCallPipeline
import io.ktor.server.application.call
import io.ktor.server.application.install
import io.ktor.server.auth.Authentication
import io.ktor.server.auth.authenticate
import io.ktor.server.auth.jwt.jwt
import io.ktor.server.auth.principal
import io.ktor.server.response.respond
import io.ktor.server.routing.routing
import org.katan.http.di.HttpModule
import org.katan.http.di.HttpModuleRegistry
import org.katan.http.response.HttpError
import org.katan.http.response.respondError
import org.katan.service.account.AccountService
import org.katan.service.auth.AuthService
import org.katan.service.auth.http.routes.login
import org.katan.service.auth.http.routes.verify
import org.katan.service.auth.http.shared.AccountKey
import org.katan.service.auth.http.shared.AccountPrincipal
import org.koin.ktor.ext.inject

internal class AuthHttpModule(registry: HttpModuleRegistry) : HttpModule(registry) {

    override fun install(app: Application) {
        app.apply {
            installRouter()
            installAuthentication()
        }
    }

    private fun Application.installRouter() {
        routing {
            login()

            authenticate {
                verify()
            }

            intercept(ApplicationCallPipeline.Call) {
                call.principal<AccountPrincipal>()?.account?.let {
                    call.attributes.put(AccountKey, it)
                }
            }
        }
    }

    private fun Application.installAuthentication() {
        val authService by inject<AuthService>()
        val accountService by inject<AccountService>()

        install(Authentication) {
            jwt {
                realm = "Katan"

                challenge { _, _ ->
                    call.respond(HttpStatusCode.Unauthorized, HttpError.InvalidAccessToken)
                }

                verifier(
                    runCatching { authService as JWTVerifier }
                        .onFailure { error("AuthService implementation must be a JWTVerifier") }
                        .getOrThrow()
                )

                validate { credentials ->
                    handleAuthentication(
                        credentials.payload.subject,
                        accountService
                    )
                }
            }
        }
    }

    private suspend fun ApplicationCall.handleAuthentication(
        jwtSubject: String,
        accountService: AccountService
    ): AccountPrincipal {
        val id = jwtSubject.toLong()
        val account = runCatching {
            accountService.getAccount(id)
        }.getOrNull() ?: respondError(HttpError.UnknownAccount)

        return AccountPrincipal(account)
    }
}
