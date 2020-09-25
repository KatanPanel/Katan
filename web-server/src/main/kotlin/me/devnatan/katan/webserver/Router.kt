package me.devnatan.katan.webserver

import io.ktor.application.*
import io.ktor.http.*
import io.ktor.http.cio.websocket.*
import io.ktor.locations.*
import io.ktor.locations.post
import io.ktor.response.*
import io.ktor.routing.*
import io.ktor.websocket.*
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.consumeAsFlow
import kotlinx.coroutines.flow.filterIsInstance
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.json.Json
import me.devnatan.katan.webserver.http.HttpResponse
import me.devnatan.katan.webserver.routes.LoginRoute
import me.devnatan.katan.webserver.routes.RegisterRoute
import me.devnatan.katan.webserver.routes.VerifyRoute
import me.devnatan.katan.webserver.websocket.message.MutableWebSocketMessage
import me.devnatan.katan.webserver.websocket.session.KtorWebSocketSession
import org.mpierce.ktor.csrf.csrfProtection

private suspend fun ApplicationCall.respondWithOk(response: Any) {
    respond(HttpStatusCode.OK, HttpResponse.Ok(response))
}

private suspend fun ApplicationCall.respondWithError(status: HttpStatusCode, response: Pair<Int, String>) {
    respond(status, HttpResponse.Error(response.first, response.second))
}

private val kws = KatanWebServer.INSTANCE

@OptIn(KtorExperimentalLocationsAPI::class, ExperimentalCoroutinesApi::class)
internal fun Application.setupRouter() = routing {
    webSocket("/") {
        val session = KtorWebSocketSession(this) {
            kws.webSocketManager.writePacket(it)
        }

        kws.webSocketManager.attachSession(session)
        try {
            incoming.consumeAsFlow().filterIsInstance<Frame.Text>().collect { frame ->
                val data = Json.decodeFromString<Map<String, Any>>(frame.readText())
                kws.webSocketManager.emitEvent(MutableWebSocketMessage(
                    data.getValue("id") as Int,
                    data.getValue("content"),
                    session
                ))
            }
        } finally {
            kws.webSocketManager.detachSession(session)
        }
    }


    route("/auth") {
        csrfProtection {
            post<LoginRoute> { account ->
                if (account.username.isBlank() || account.password.isBlank()) {
                    call.respondWithError(HttpStatusCode.BadRequest, ACCOUNT_INVALID_CREDENTIALS_ERROR)
                    return@post
                }

                try {
                    call.respondWithOk(
                        mapOf(
                            "token" to kws.accountManager.authenticateAccount(
                                account.username,
                                account.password
                            )
                        )
                    )
                } catch (e: IllegalArgumentException) {
                    call.respondWithError(HttpStatusCode.BadRequest, ACCOUNT_INVALID_CREDENTIALS_ERROR)
                } catch (e: NoSuchElementException) {
                    call.respondWithError(HttpStatusCode.BadRequest, ACCOUNT_NOT_FOUND_ERROR)
                }
            }

            post<RegisterRoute> { account ->
                if (account.username.isBlank() || account.password.isBlank()) {
                    call.respondWithError(HttpStatusCode.BadRequest, ACCOUNT_INVALID_CREDENTIALS_ERROR)
                    return@post
                }

                if (kws.accountManager.existsAccount(account.username)) {
                    call.respondWithError(HttpStatusCode.Conflict, ACCOUNT_ALREADY_EXISTS_ERROR)
                    return@post
                }

                val entity = kws.accountManager.createAccount(account.username, account.password)
                kws.accountManager.registerAccount(entity)
                call.respondWithOk(mapOf("account" to entity))
            }
        }

        post<VerifyRoute> { verify ->
            if (verify.token.isBlank()) {
                call.respondWithError(HttpStatusCode.BadRequest, INVALID_ACCESS_TOKEN_ERROR)
                return@post
            }

            try {
                call.respondWithOk(mapOf("account" to kws.accountManager.verifyToken(verify.token)))
            } catch (e: IllegalArgumentException) {
                call.respondWithError(HttpStatusCode.BadRequest, INVALID_ACCESS_TOKEN_ERROR)
            }
        }
    }
}