package me.devnatan.katan.webserver.environment

import io.ktor.application.*
import io.ktor.auth.*
import io.ktor.auth.jwt.*
import io.ktor.http.*
import io.ktor.http.cio.websocket.*
import io.ktor.locations.*
import io.ktor.request.*
import io.ktor.response.*
import io.ktor.routing.*
import io.ktor.util.pipeline.*
import io.ktor.websocket.*
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.consumeAsFlow
import kotlinx.coroutines.flow.filterIsInstance
import me.devnatan.katan.common.account.SecureAccount
import me.devnatan.katan.webserver.*
import me.devnatan.katan.webserver.environment.exceptions.KatanHTTPException
import me.devnatan.katan.webserver.environment.routes.AuthRoute
import me.devnatan.katan.webserver.environment.routes.IndexRoute
import me.devnatan.katan.webserver.serializable.SerializableAccount
import me.devnatan.katan.webserver.websocket.session.KtorWebSocketSession
import java.util.*

internal suspend fun PipelineContext<*, ApplicationCall>.respondWithOk(
    vararg response: Pair<Any, Any>,
    status: HttpStatusCode = HttpStatusCode.OK,
) {
    call.respond(
        status, mapOf(
            "response" to "success",
            "data" to mapOf(*response)
        )
    )
}

internal fun respondWithError(
    response: Pair<Int, String>,
    status: HttpStatusCode = HttpStatusCode.BadRequest,
): Nothing = throw KatanHTTPException(response, status)

@OptIn(KtorExperimentalLocationsAPI::class, ExperimentalCoroutinesApi::class)
fun Application.router(
    env: Environment
) = routing {
    intercept(ApplicationCallPipeline.Fallback) {
        call.respond(HttpStatusCode.NotFound)
    }

    webSocket("/") {
        val session = KtorWebSocketSession(this) {
            outgoing.send(
                Frame.Text(
                    env.webSocketManager.objectMapper.writeValueAsString(
                        mapOf(
                            "op" to it.op,
                            "d" to it.content
                        )
                    )
                )
            )
        }

        env.webSocketManager.attachSession(session)
        try {
            while (true) {
                val frame = incoming.receiveOrNull() ?: break
                when (frame) {
                    is Frame.Close -> break
                    is Frame.Text -> env.webSocketManager.readPacket(session, frame)
                    else -> throw UnsupportedOperationException("Unsupported frame type")
                }
            }
        } catch (_: ClosedReceiveChannelException) {
        } catch (e: Throwable) {
            e.printStackTrace()
        } finally {
            env.webSocketManager.detachSession(session)
        }
    }

    get<IndexRoute> {
        call.respondText("Welcome to Katan Web Server!")
    }

    post<AuthRoute.LoginRoute> {
        val data = call.receive<Map<String, String>>()
        val username = data["username"]
        if (username == null || username.isBlank())
            respondWithError(ACCOUNT_MISSING_CREDENTIALS_ERROR)

        val account = env.server.katan.accountManager.getAccount(username)
            ?: respondWithError(ACCOUNT_NOT_FOUND_ERROR)

        val token = try {
            env.server.internalAccountManager.authenticateAccount(
                account,
                data.getValue("password")
            )
        } catch (e: IllegalArgumentException) {
            respondWithError(ACCOUNT_INVALID_CREDENTIALS_ERROR)
        }

        respondWithOk("token" to token)
    }

    post<AuthRoute.RegisterRoute> {
        val account = call.receive<Map<String, String>>()
        val username = account["username"]
        if (username == null || username.isBlank())
            respondWithError(ACCOUNT_INVALID_CREDENTIALS_ERROR)

        if (env.server.accountManager.existsAccount(username))
            respondWithError(ACCOUNT_ALREADY_EXISTS_ERROR)

        val entity = env.server.accountManager.createAccount(username, account.getValue("password"))
        env.server.accountManager.registerAccount(entity)
        respondWithOk("account" to entity)
    }

    get<AuthRoute.VerifyRoute> { verify ->
        if (verify.token.isBlank())
            respondWithError(INVALID_ACCESS_TOKEN_ERROR)

        runCatching {
            env.server.internalAccountManager.verifyToken(verify.token)
        }.onSuccess {
            respondWithOk("account" to SerializableAccount(it as SecureAccount))
        }.onFailure { respondWithError(INVALID_ACCESS_TOKEN_ERROR) }
    }

    route("/servers") {
        handle {
            val principal = call.authentication.principal<JWTPrincipal>()
                ?: respondWithError(INVALID_ACCESS_TOKEN_ERROR, HttpStatusCode.Unauthorized)

            val claim = principal.payload.getClaim("account")
            if (claim.isNull)
                respondWithError(INVALID_ACCESS_TOKEN_ERROR, HttpStatusCode.Unauthorized)

            val account = env.server.accountManager.getAccount(UUID.fromString(claim.asString()))
                ?: respondWithError(ACCOUNT_NOT_FOUND_ERROR)

            val token = env.server.internalAccountManager.getCachedAccountToken(account.id.toString())
                ?: respondWithError(INVALID_SESSION_ERROR)

            respondWithError(ACCOUNT_NOT_FOUND_ERROR)
        }
    }
}