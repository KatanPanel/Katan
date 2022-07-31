package org.katan.service.auth.http.routes

import io.ktor.server.resources.post
import io.ktor.server.routing.Route
import org.katan.http.respond
import org.katan.http.respondError
import org.katan.service.auth.AuthService
import org.katan.service.auth.http.AuthResource
import org.katan.service.auth.http.InvalidAccessTokenError
import org.katan.service.auth.http.dto.VerificationResponse
import org.koin.ktor.ext.inject

internal fun Route.verify() {
    val authService by inject<AuthService>()

    post<AuthResource.Verify> { req ->
        if (req.token == null)
             respondError(InvalidAccessTokenError)

        val account = runCatching {
            authService.verify(req.token)
        }.getOrNull() ?: respondError(InvalidAccessTokenError)

        respond(VerificationResponse(account))
    }
}