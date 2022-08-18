package org.katan.service.auth.http.routes

import io.ktor.server.application.call
import io.ktor.server.auth.principal
import io.ktor.server.resources.get
import io.ktor.server.routing.Route
import org.katan.http.response.respond
import org.katan.service.account.http.dto.AccountResponse
import org.katan.service.auth.http.AuthResource
import org.katan.service.auth.http.dto.VerifyResponse
import org.katan.service.auth.http.shared.AccountPrincipal

internal fun Route.verify() {
    get<AuthResource.Verify> {
        val account = call.principal<AccountPrincipal>()!!.account

        respond(VerifyResponse(AccountResponse(account)))
    }
}
