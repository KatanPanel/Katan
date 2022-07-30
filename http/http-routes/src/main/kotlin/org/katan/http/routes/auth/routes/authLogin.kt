package org.katan.http.routes.auth.routes

import io.ktor.server.application.call
import io.ktor.server.request.receive
import io.ktor.server.resources.post
import io.ktor.server.routing.Route
import org.katan.http.respond
import org.katan.http.routes.auth.AuthResource
import org.katan.http.routes.auth.dto.AuthLoginRequest
import org.katan.http.routes.auth.dto.AuthLoginResponse
import org.katan.service.auth.AuthService
import org.koin.ktor.ext.inject

internal fun Route.authLogin() {
    val authService by inject<AuthService>()

    post<AuthResource.Login> {
        val req = call.receive<AuthLoginRequest>()
        val token = authService.auth(req.username, req.password)

        respond(AuthLoginResponse(token))
    }
}