package org.katan.service.auth.http

import com.auth0.jwt.interfaces.JWTVerifier
import io.ktor.http.HttpStatusCode
import io.ktor.server.application.Application
import io.ktor.server.application.install
import io.ktor.server.auth.Authentication
import io.ktor.server.auth.authenticate
import io.ktor.server.auth.jwt.jwt
import io.ktor.server.routing.routing
import org.katan.http.HttpError
import org.katan.http.HttpModule
import org.katan.http.HttpModuleRegistry
import org.katan.http.respondError
import org.katan.service.account.AccountService
import org.katan.service.auth.AuthService
import org.katan.service.auth.http.routes.login
import org.katan.service.auth.http.routes.verify
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
        }
    }

    private fun Application.installAuthentication() {
        val authService by inject<AuthService>()
        val accountService by inject<AccountService>()
        install(Authentication) {
            jwt {
                realm = "Katan"

                challenge { _, _ ->
                    respondError(
                        HttpError.InvalidAccessToken,
                        HttpStatusCode.Unauthorized
                    )
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

    private suspend fun handleAuthentication(
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
