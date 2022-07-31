package org.katan.service.auth.http.routes

import io.ktor.server.application.call
import io.ktor.server.auth.principal
import io.ktor.server.resources.get
import io.ktor.server.routing.Route
import org.katan.http.respond
import org.katan.service.auth.http.AccountPrincipal
import org.katan.service.auth.http.AuthResource
import org.katan.service.auth.http.dto.VerificationResponse

internal fun Route.verify() {
    get<AuthResource.Verify> {
        respond(VerificationResponse(call.principal<AccountPrincipal>()!!.account))
    }
}
