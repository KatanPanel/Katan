package org.katan.service.auth.http

import io.ktor.server.application.Application
import io.ktor.server.application.install
import io.ktor.server.auth.Authentication
import io.ktor.server.auth.jwt.jwt
import io.ktor.server.routing.routing
import org.katan.http.HttpModule
import org.katan.http.HttpModuleRegistry
import org.katan.http.respondError
import org.katan.service.auth.AuthService
import org.katan.service.auth.http.routes.login
import org.katan.service.auth.http.routes.verify
import org.koin.ktor.ext.inject

internal class AuthHttpModule(registry: HttpModuleRegistry) : HttpModule(registry) {

    override fun install(app: Application) {
        app.apply {
            routing {
                login()
                verify()
            }

            val authService by inject<AuthService>()
            install(Authentication) {
                jwt {
                    realm = "Katan"

                    validate { credentials ->
                        val account =
                            credentials.payload.subject?.let {
                                authService.verify(it)
                            } ?: respondError(org.katan.http.AccountNotFound)

                        AccountPrincipal(account)
                    }
                }
            }
        }
    }
}
