package me.devnatan.katan.webserver

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
import me.devnatan.katan.api.Katan
import me.devnatan.katan.webserver.exceptions.KatanHTTPException
import me.devnatan.katan.webserver.routes.*
import me.devnatan.katan.webserver.websocket.WebSocketManager
import me.devnatan.katan.webserver.websocket.WebSocketOpCode.DATA_KEY
import me.devnatan.katan.webserver.websocket.WebSocketOpCode.OP_KEY
import me.devnatan.katan.webserver.websocket.session.KtorWebSocketSession

internal suspend fun PipelineContext<*, ApplicationCall>.respondOk(
    response: Any,
    status: HttpStatusCode = HttpStatusCode.OK,
) = call.respond(
    status, mapOf(
        "response" to "success",
        "data" to response
    )
)

internal suspend fun PipelineContext<*, ApplicationCall>.respondOk(
    vararg response: Pair<Any, Any>,
    status: HttpStatusCode = HttpStatusCode.OK,
) = respondOk(response.toMap(), status)

internal fun respondWithError(
    response: Pair<Int, String>,
    status: HttpStatusCode = HttpStatusCode.BadRequest,
): Nothing = throw KatanHTTPException(response, status)

@OptIn(ExperimentalCoroutinesApi::class)
fun Routing.installWebSocketRoute(webSocketManager: WebSocketManager) {
    webSocket("/") {
        val session = KtorWebSocketSession(this) {
            outgoing.send(
                Frame.Text(
                    webSocketManager.objectMapper.writeValueAsString(
                        mapOf(
                            OP_KEY to it.op,
                            DATA_KEY to it.content
                        )
                    )
                )
            )
        }

        webSocketManager.attachSession(session)
        println("$session attached")
        try {
            for (frame in incoming) {
                when (frame) {
                    is Frame.Close -> break
                    is Frame.Text -> webSocketManager.readPacket(session, frame)
                    else -> throw UnsupportedOperationException("Unsupported frame type")
                }
            }
        } catch (_: ClosedReceiveChannelException) {
        } catch (e: Throwable) {
            e.printStackTrace()
        } finally {
            webSocketManager.detachSession(session)
            println("$session detached")
        }
    }
}

@OptIn(KtorExperimentalLocationsAPI::class, ExperimentalCoroutinesApi::class)
fun Application.router(
    env: Environment
) = routing {
    /* intercept(ApplicationCallPipeline.Fallback) {
        call.respond(HttpStatusCode.NotFound)
    } */

    installWebSocketRoute(env.webSocketManager)

    get<IndexRoute> {
        respondOk("version" to Katan.VERSION.toString())
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

        respondOk("token" to token)
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
        respondOk("account" to entity)
    }

    authenticate {
        get<InfoRoute> {
            respondOk(
                "version" to Katan.VERSION,
                "platform" to env.server.katan.platform,
                "environment" to env.server.katan.environment,
                "plugins" to env.server.katan.pluginManager.getPlugins().map {
                    it.serialize()
                },
                "games" to env.server.katan.gameManager.getRegisteredGames(),
            )
        }

        get<AuthRoute.Verify> {
            respondOk("account" to call.account.serialize(env.server.katan.permissionManager))
        }

        /* start: servers */
        get<ServersRoute> {
            respondOk(env.server.serverManager.getServerList().map { it.serialize() })
        }

        get<ServersRoute.Server> { data ->
            respondOk("server" to data.server.serialize())
        }
        /* end: servers */

        /* start: accounts */
        get<AccountsRoute> {
            respondOk(env.server.accountManager.getAccounts().map { it.serialize(env.server.katan.permissionManager) })
        }
        /* end: accounts */
    }
}