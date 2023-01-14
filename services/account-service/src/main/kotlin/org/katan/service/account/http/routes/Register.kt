package org.katan.service.account.http.routes

import io.ktor.http.HttpStatusCode
import io.ktor.server.application.call
import io.ktor.server.request.receive
import io.ktor.server.resources.post
import io.ktor.server.routing.Route
import jakarta.validation.Validator
import org.katan.http.response.HttpError
import org.katan.http.response.respond
import org.katan.http.response.respondError
import org.katan.http.response.validateOrThrow
import org.katan.service.account.AccountConflictException
import org.katan.service.account.AccountService
import org.katan.service.account.http.AccountRoutes
import org.katan.service.account.http.dto.AccountResponse
import org.katan.service.account.http.dto.RegisterRequest
import org.katan.service.account.http.dto.RegisterResponse
import org.koin.ktor.ext.inject

internal fun Route.register() {
    val accountService by inject<AccountService>()
    val validator by inject<Validator>()

    post<AccountRoutes.Register> {
        val req = call.receive<RegisterRequest>()
        validator.validateOrThrow(req)

        val account = try {
            accountService.createAccount(
                username = req.username,
                displayName = req.displayName,
                email = req.email,
                password = req.password
            )
        } catch (e: AccountConflictException) {
            respondError(HttpError.AccountLoginConflict, HttpStatusCode.Conflict)
        }

        respond(RegisterResponse(AccountResponse(account)))
    }
}
