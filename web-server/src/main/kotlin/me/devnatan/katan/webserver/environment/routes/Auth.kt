@file:OptIn(KtorExperimentalLocationsAPI::class)

package me.devnatan.katan.webserver.environment.routes

import io.ktor.locations.*

@Location("/auth")
class AuthRoute {

    @Location("/login")
    data class Login(val parent: AuthRoute)

    @Location("/register")
    data class Register(val parent: AuthRoute)

    @Location("/verify")
    data class Verify(val parent: AuthRoute)

}