@file:OptIn(KtorExperimentalLocationsAPI::class)

package me.devnatan.katan.bootstrap.routes

import io.ktor.locations.*

@Location("/login")
data class LoginRoute(val username: String, val password: String)

@Location("/register")
data class RegisterRoute(val username: String, val password: String)

@Location("/verify")
data class VerifyRoute(val token: String)