package org.katan.service.auth.http.routes

import io.ktor.server.resources.post
import io.ktor.server.routing.Route
import org.katan.http.respond
import org.katan.http.respondError
import org.katan.model.security.AuthenticationException
import org.katan.model.security.InvalidAccessTokenException
import org.katan.service.auth.AuthService
import org.katan.service.auth.http.AuthResource
import org.katan.service.auth.http.GenericAuthenticationError
import org.katan.service.auth.http.InvalidAccessTokenError
import org.katan.service.auth.http.dto.VerificationResponse
import org.koin.ktor.ext.inject

internal fun Route.verify() {
    val authService by inject<AuthService>()

    post<AuthResource.Verify> { req ->
        println("request token: ${req.token}")
        if (req.token == null) {
            respondError(InvalidAccessTokenError)
        }

        val account = try {
            authService.verify(req.token)
        } catch (e: InvalidAccessTokenException) {
            respondError(InvalidAccessTokenError)
        } catch (e: AuthenticationException) {
            respondError(GenericAuthenticationError(e.message!!))
        } ?: respondError(InvalidAccessTokenError)

        respond(VerificationResponse(account))
    }
}
