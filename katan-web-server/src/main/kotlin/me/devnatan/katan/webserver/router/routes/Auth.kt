package me.devnatan.katan.webserver.router.routes

import io.ktor.application.*
import io.ktor.locations.*
import io.ktor.request.*
import io.ktor.routing.*
import me.devnatan.katan.webserver.*
import me.devnatan.katan.webserver.router.locations.AuthRoute
import me.devnatan.katan.webserver.router.locations.account
import me.devnatan.katan.webserver.util.respondError
import me.devnatan.katan.webserver.util.respondOk

@KtorExperimentalLocationsAPI
fun Route.authRoutes(ws: KatanWS) {
    post<AuthRoute.Register> {
        val account = call.receive<Map<String, String>>()
        val username = account["username"]
        if (username == null || username.isBlank())
            respondError(ACCOUNT_INVALID_CREDENTIALS_ERROR)

        if (ws.katan.accountManager.existsAccount(username))
            respondError(ACCOUNT_ALREADY_EXISTS_ERROR)

        val entity = ws.katan.accountManager.createAccount(
            username,
            account.getValue("password")
        )
        ws.katan.accountManager.registerAccount(entity)
        respondOk("account" to entity)
    }

    get<AuthRoute.Verify> {
        respondOk("account" to call.account)
    }
}

@KtorExperimentalLocationsAPI
fun Route.authLoginRoute(ws: KatanWS) {
    post<AuthRoute.Login> {
        val data = call.receive<Map<String, String>>()
        val username = data["username"]
        if (username == null || username.isBlank())
            respondError(ACCOUNT_MISSING_CREDENTIALS_ERROR)

        val account = ws.katan.accountManager.getAccount(username)
            ?: respondError(ACCOUNT_NOT_FOUND_ERROR)

        val token = try {
            ws.tokenManager.authenticateAccount(
                account,
                data.getValue("password")
            )
        } catch (e: IllegalArgumentException) {
            respondError(ACCOUNT_INVALID_CREDENTIALS_ERROR)
        }

        respondOk("token" to token)
    }
}