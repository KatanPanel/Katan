package org.katan.service.account.http.routes

import io.ktor.http.HttpStatusCode
import io.ktor.server.application.call
import io.ktor.server.request.receive
import io.ktor.server.resources.post
import io.ktor.server.routing.Route
import org.katan.http.HttpError
import org.katan.http.respond
import org.katan.http.respondError
import org.katan.service.account.AccountConflictException
import org.katan.service.account.AccountService
import org.katan.service.account.http.AccountRoutes
import org.katan.service.account.http.dto.AccountResponse
import org.katan.service.account.http.dto.RegisterRequest
import org.katan.service.account.http.dto.RegisterResponse
import org.koin.ktor.ext.inject

internal fun Route.register() {
    val accountService by inject<AccountService>()

    post<AccountRoutes.Register> {
        val req = call.receive<RegisterRequest>()

        val account = try {
            accountService.createAccount(req.username, req.password)
        } catch (e: AccountConflictException) {
            respondError(HttpError.AccountUsernameConflict, HttpStatusCode.Conflict)
        }

        respond(RegisterResponse(AccountResponse(account)))
    }
}
