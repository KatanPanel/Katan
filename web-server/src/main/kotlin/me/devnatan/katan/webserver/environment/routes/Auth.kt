@file:OptIn(KtorExperimentalLocationsAPI::class)

package me.devnatan.katan.webserver.environment.routes

import io.ktor.locations.*

@Location("/auth")
class AuthRoute {

    @Location("/login")
    data class LoginRoute(val parent: AuthRoute)

    @Location("/register")
    data class RegisterRoute(val parent: AuthRoute)

    @Location("/verify")
    data class VerifyRoute(val parent: AuthRoute)

}