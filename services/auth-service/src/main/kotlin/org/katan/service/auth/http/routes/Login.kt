package org.katan.service.auth.http.routes

import io.ktor.server.application.call
import io.ktor.server.request.receive
import io.ktor.server.resources.post
import io.ktor.server.routing.Route
import org.katan.http.respond
import org.katan.http.respondError
import org.katan.model.account.AccountNotFoundException
import org.katan.model.security.InvalidCredentialsException
import org.katan.service.account.http.AccountNotFound
import org.katan.service.auth.AuthService
import org.katan.service.auth.http.AuthResource
import org.katan.service.auth.http.InvalidCredentialsError
import org.katan.service.auth.http.dto.LoginRequest
import org.katan.service.auth.http.dto.LoginResponse
import org.koin.ktor.ext.inject

internal fun Route.login() {
    val authService by inject<AuthService>()

    post<AuthResource.Login> {
        val req = call.receive<LoginRequest>()
        val token = try {
            authService.auth(req.username, req.password)
        } catch (e: Throwable) {
            when (e) {
                is AccountNotFoundException -> respondError(AccountNotFound)
                is InvalidCredentialsException -> respondError(InvalidCredentialsError)
                else -> throw e
            }
        }

        respond(LoginResponse(token))
    }
}
