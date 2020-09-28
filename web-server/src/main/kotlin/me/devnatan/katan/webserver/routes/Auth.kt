@file:OptIn(KtorExperimentalLocationsAPI::class)

package me.devnatan.katan.webserver.routes

import io.ktor.locations.*

@Location("/auth/login")
data class LoginRoute(val username: String, val password: String)

@Location("/auth/register")
data class RegisterRoute(val username: String, val password: String)

@Location("/auth/verify")
data class VerifyRoute(val token: String)