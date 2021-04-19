@file:OptIn(KtorExperimentalLocationsAPI::class)

package me.devnatan.katan.webserver.routing.locations

import io.ktor.application.*
import io.ktor.auth.*
import io.ktor.locations.*
import me.devnatan.katan.api.security.account.Account
import me.devnatan.katan.webserver.jwt.AccountPrincipal

@Location("/auth")
class AuthRoute {

    @Location("/login")
    data class Login(val parent: AuthRoute)

    @Location("/register")
    data class Register(val parent: AuthRoute)

    @Location("/verify")
    data class Verify(val parent: AuthRoute)

}

val ApplicationCall.account: Account
    get() = authentication.principal<AccountPrincipal>()!!.account