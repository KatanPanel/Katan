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
    val ws = env.server
    val katan = ws.katan

    routing {
        webSocket("/") {
            env.webSocketManager.handle(this)
        }

        get<IndexRoute> {
            respondOk(
                "version" to Katan.VERSION,
                "version_plain" to Katan.VERSION.toString()
            )
        }

        post<AuthRoute.Login> {
            val data = call.receive<Map<String, String>>()
            val username = data["username"]
            if (username == null || username.isBlank())
                respondWithError(ACCOUNT_MISSING_CREDENTIALS_ERROR)

            val account = ws.katan.accountManager.getAccount(username)
                ?: respondWithError(ACCOUNT_NOT_FOUND_ERROR)

            val token = try {
                ws.internalAccountManager.authenticateAccount(
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

            val entity = ws.accountManager.createAccount(
                username,
                account.getValue("password")
            )
            ws.accountManager.registerAccount(entity)
            respondOk("account" to entity)
        }

        authenticate {
            get<InfoRoute> {
                respondOk(
                    "version" to Katan.VERSION,
                    "version_plain" to Katan.VERSION.toString(),
                    "platform" to katan.platform,
                    "environment" to katan.environment.toString(),
                    "locale" to katan.translator.locale.toLanguageTag(),
                    "oauth" to env.server.katan.serviceManager.get<ExternalAuthenticationProvider>()
                        .map { it.id }
                )
            }

            get<InfoRoute.Accounts> {
                respondOk("accounts" to katan.accountManager.getAccounts())
            }

            get<InfoRoute.Games> {
                respondOk("games" to katan.gameManager.getRegisteredGames())
            }

            get<InfoRoute.Plugins> {
                respondOk("plugins" to katan.pluginManager.getPlugins())
            }

            get<InfoRoute.Permissions> {
                respondOk("permissions" to katan.permissionManager.getRegisteredPermissionKeys())
            }

            get<AuthRoute.Verify> {
                respondOk("account" to call.account)
            }

            get<ServersRoute> {
                respondOk("servers" to ws.serverManager.getServerList())
            }

            get<ServersRoute.Server> { (server) ->
                respondOk("server" to server)
            }

            get<ServersRoute.Server.Start> { (parent) ->
                katan.serverManager.startServer(parent.server)
                call.respond(HttpStatusCode.NoContent)
            }

            get<ServersRoute.Server.Stop> { (parent) ->
                katan.serverManager.stopServer(
                    parent.server,
                    Duration.ofSeconds(
                        call.parameters["timeout"]?.toLongOrNull() ?: 10
                    )
                )
                call.respond(HttpStatusCode.NoContent)
            }

            get<ServersRoute.Server.FileSystem> { (parent) ->
                respondOk("disks" to katan.internalFs.listDisks(parent.server))
            }

            get<ServersRoute.Server.FileSystemDisk> { (disk, parent) ->
                val impl = katan.internalFs.getDisk(
                    parent.server,
                    disk
                ) ?: respondWithError(SERVER_FS_DISK_NOT_FOUND)

                respondOk("disk" to impl)
            }

            get<ServersRoute.Server.FileSystemDiskFiles> { (disk, parent) ->
                val impl = katan.internalFs.getDisk(
                    parent.server,
                    disk
                )
                    ?: respondWithError(SERVER_FS_DISK_NOT_FOUND)

                respondOk(
                    "files" to impl.listFiles()
                )
            }
        }
    }
}