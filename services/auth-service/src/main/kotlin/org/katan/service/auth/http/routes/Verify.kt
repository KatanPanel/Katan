package org.katan.service.auth.http.routes

import io.ktor.server.application.call
import io.ktor.server.auth.principal
import io.ktor.server.resources.get
import io.ktor.server.routing.Route
import org.katan.http.respond
import org.katan.service.account.http.dto.AccountResponse
import org.katan.service.auth.http.AccountPrincipal
import org.katan.service.auth.http.AuthResource

internal fun Route.verify() {
    get<AuthResource.Verify> {
        respond(AccountResponse(call.principal<AccountPrincipal>()!!.account))
    }
}
