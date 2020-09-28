@file:OptIn(KtorExperimentalLocationsAPI::class)

package me.devnatan.katan.webserver.routes

import io.ktor.locations.*

@Location("/auth")
class AuthRoute {

    @Location("/login")
    data class LoginRoute(
        val username: String = "",
        val password: String = "",
        val parent: AuthRoute
    )

    @Location("/register")
    data class RegisterRoute(
        val username: String = "",
        val password: String = "",
        val parent: AuthRoute
    )

    @Location("/verify")
    data class VerifyRoute(
        val token: String = "",
        val parent: AuthRoute
    )

}