package org.katan.service.auth.http.routes

import io.ktor.server.application.call
import io.ktor.server.request.receive
import io.ktor.server.resources.post
import io.ktor.server.routing.Route
import jakarta.validation.Validator
import org.katan.http.response.HttpError
import org.katan.http.response.respond
import org.katan.http.response.respondError
import org.katan.http.response.validateOrThrow
import org.katan.model.account.AccountNotFoundException
import org.katan.model.security.InvalidCredentialsException
import org.katan.service.auth.AuthService
import org.katan.service.auth.http.AuthResource
import org.katan.service.auth.http.dto.LoginRequest
import org.katan.service.auth.http.dto.LoginResponse
import org.koin.ktor.ext.inject

internal fun Route.login() {
    val authService by inject<AuthService>()
    val validator by inject<Validator>()

    post<AuthResource.Login> {
        val req = call.receive<LoginRequest>()
        validator.validateOrThrow(req)

        val token = try {
            authService.auth(req.username!!, req.password!!)
        } catch (e: Throwable) {
            when (e) {
                is AccountNotFoundException -> respondError(HttpError.UnknownAccount)
                is InvalidCredentialsException -> respondError(HttpError.AccountInvalidCredentials)
                else -> throw e
            }
        }

        respond(LoginResponse(token))
    }
}
