package me.devnatan.katan.webserver.environment

import io.ktor.application.*
import io.ktor.auth.*
import io.ktor.http.*
import io.ktor.http.cio.websocket.*
import io.ktor.locations.*
import io.ktor.request.*
import io.ktor.response.*
import io.ktor.routing.*
import io.ktor.util.pipeline.*
import io.ktor.websocket.*
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.channels.ClosedReceiveChannelException
import kotlinx.coroutines.channels.receiveOrNull
import me.devnatan.katan.api.Katan
import me.devnatan.katan.webserver.*
import me.devnatan.katan.webserver.environment.exceptions.KatanHTTPException
import me.devnatan.katan.webserver.environment.jwt.AccountPrincipal
import me.devnatan.katan.webserver.environment.routes.AuthRoute
import me.devnatan.katan.webserver.environment.routes.IndexRoute
import me.devnatan.katan.webserver.environment.routes.ServersRoute
import me.devnatan.katan.webserver.serializable.serializable
import me.devnatan.katan.webserver.websocket.session.KtorWebSocketSession

internal suspend fun PipelineContext<*, ApplicationCall>.respondWithOk(
    vararg response: Pair<Any, Any>,
    status: HttpStatusCode = HttpStatusCode.OK,
) {
    call.respond(
        status, mapOf(
            "response" to "success",
            "data" to response.toMap()
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
        respondWithOk("version" to Katan.VERSION.toString())
    }

    post<AuthRoute.Login> {
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

    post<AuthRoute.Register> {
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

    get<AuthRoute.Verify> {
        val account = call.authentication.principal<AccountPrincipal>()?.account
            ?: respondWithError(INVALID_ACCESS_TOKEN_ERROR, HttpStatusCode.Unauthorized)

        respondWithOk("account" to account)
    }

    route("") {
        intercept(ApplicationCallPipeline.Features) {
            call.authentication.principal<AccountPrincipal>() ?: respondWithError(
                INVALID_ACCESS_TOKEN_ERROR,
                HttpStatusCode.Unauthorized
            )
        }

        get<ServersRoute> {
            respondWithOk("servers" to env.server.serverManager.getServerList().map { it.serializable() })
        }

        get<ServersRoute.Server> { data ->
            respondWithOk("server" to data.server.serializable())
        }
    }
}