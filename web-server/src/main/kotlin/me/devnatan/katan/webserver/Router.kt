package me.devnatan.katan.webserver

import io.ktor.application.*
import io.ktor.auth.*
import io.ktor.http.*
import io.ktor.locations.*
import io.ktor.request.*
import io.ktor.response.*
import io.ktor.routing.*
import io.ktor.util.pipeline.*
import io.ktor.websocket.*
import kotlinx.coroutines.ExperimentalCoroutinesApi
import me.devnatan.katan.api.Katan
import me.devnatan.katan.api.security.auth.ExternalAuthenticationProvider
import me.devnatan.katan.api.service.get
import me.devnatan.katan.webserver.exceptions.KatanHTTPException
import me.devnatan.katan.webserver.routes.*
import java.time.Duration

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

@OptIn(KtorExperimentalLocationsAPI::class, ExperimentalCoroutinesApi::class)
fun Application.router(env: Environment) {
    val server = env.server
    val katan = server.katan

    routing {
        webSocket("/") {
            env.webSocketManager.handle(this)
        }

        get<IndexRoute> {
            respondOk(
                "version" to Katan.VERSION.toString(),
                "oauth" to env.server.katan.serviceManager.get<ExternalAuthenticationProvider>().map { it.id }
            )
        }

        post<AuthRoute.Login> {
            val data = call.receive<Map<String, String>>()
            val username = data["username"]
            if (username == null || username.isBlank())
                respondWithError(ACCOUNT_MISSING_CREDENTIALS_ERROR)

            val account = server.katan.accountManager.getAccount(username)
                ?: respondWithError(ACCOUNT_NOT_FOUND_ERROR)

            val token = try {
                server.internalAccountManager.authenticateAccount(
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

            val entity = server.accountManager.createAccount(username, account.getValue("password"))
            server.accountManager.registerAccount(entity)
            respondOk("account" to entity)
        }

        authenticate {
            get<InfoRoute> {
                respondOk(
                    "version" to Katan.VERSION,
                    "platform" to katan.platform,
                    "environment" to katan.environment,
                    "plugins" to katan.pluginManager.getPlugins().map {
                        it.serialize()
                    },
                    "games" to katan.gameManager.getRegisteredGames(),
                )
            }

            get<AuthRoute.Verify> {
                respondOk("account" to call.account.serialize(katan.permissionManager))
            }

            /* start: servers */
            get<ServersRoute> {
                respondOk(server.serverManager.getServerList().map { it.serialize() })
            }

            get<ServersRoute.Server> { data ->
                respondOk("server" to data.server.serialize())
            }

            get<ServersRoute.Server.Start> { data ->
                katan.serverManager.startServer(data.parent.server)
                call.respond(HttpStatusCode.NoContent)
            }

            get<ServersRoute.Server.Stop> { data ->
                katan.serverManager.stopServer(data.parent.server, Duration.ofSeconds(10))
                call.respond(HttpStatusCode.NoContent)
            }

            get<ServersRoute.Server.FS> { data ->
                respondOk("disks" to katan.internalFs.listDisks(data.parent.server))
            }
            /* end: servers */

            /* start: accounts */
            get<AccountsRoute> {
                respondOk(server.accountManager.getAccounts().map { it.serialize(server.katan.permissionManager) })
            }
            /* end: accounts */
        }
    }
}