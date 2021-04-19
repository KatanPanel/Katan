package me.devnatan.katan.webserver

import io.ktor.application.*
import io.ktor.auth.*
import io.ktor.http.*
import io.ktor.locations.*
import io.ktor.request.*
import io.ktor.response.*
import io.ktor.routing.*
import io.ktor.websocket.*
import kotlinx.coroutines.ExperimentalCoroutinesApi
import me.devnatan.katan.api.Katan
import me.devnatan.katan.api.security.auth.ExternalAuthenticationProvider
import me.devnatan.katan.api.service.get
import me.devnatan.katan.webserver.routing.*
import me.devnatan.katan.webserver.routing.locations.AuthRoute
import me.devnatan.katan.webserver.routing.locations.account
import me.devnatan.katan.webserver.util.respondError
import me.devnatan.katan.webserver.util.respondOk
import java.time.Duration

@OptIn(KtorExperimentalLocationsAPI::class, ExperimentalCoroutinesApi::class)
fun Application.router(ws: KatanWS) {
    routing {
        webSocket("/") {
            ws.webSocketManager.handleSession(this)
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

        authenticate {
            get<InfoRoute> {
                respondOk(
                    "version" to Katan.VERSION,
                    "version_plain" to Katan.VERSION.toString(),
                    "platform" to ws.katan.platform,
                    "environment" to ws.katan.environment.toString(),
                    "locale" to ws.katan.translator.locale.toLanguageTag(),
                    "oauth" to ws.katan.serviceManager
                        .get<ExternalAuthenticationProvider>()
                        .map { it.id }
                )
            }

            get<InfoRoute.Accounts> {
                respondOk("accounts" to ws.katan.accountManager.getAccounts())
            }

            get<InfoRoute.Games> {
                respondOk("games" to ws.katan.gameManager.getRegisteredGames())
            }

            get<InfoRoute.Plugins> {
                respondOk("plugins" to ws.katan.pluginManager.getPlugins())
            }

            get<InfoRoute.Permissions> {
                respondOk(
                    "permissions" to ws.katan.permissionManager
                        .getRegisteredPermissionKeys()
                )
            }

            get<AuthRoute.Verify> {
                respondOk("account" to call.account)
            }

            get<ServersRoute> {
                respondOk("servers" to ws.katan.serverManager.getServerList())
            }

            get<ServersRoute.Server> { (server) ->
                respondOk("server" to server)
            }

            get<ServersRoute.Server.Start> { (parent) ->
                ws.katan.serverManager.startServer(parent.server)
                call.respond(HttpStatusCode.NoContent)
            }

            get<ServersRoute.Server.Stop> { (parent) ->
                ws.katan.serverManager.stopServer(
                    parent.server,
                    Duration.ofSeconds(
                        call.parameters["timeout"]?.toLongOrNull() ?: 10
                    )
                )
                call.respond(HttpStatusCode.NoContent)
            }

            get<ServersRoute.Server.FileSystem> { (parent) ->
                respondOk(
                    "disks" to ws.katan.fs.listDisks(
                        parent
                            .server
                    )
                )
            }

            get<ServersRoute.Server.FileSystemDisk> { (disk, parent) ->
                val impl = ws.katan.fs.getDisk(
                    parent.server,
                    disk
                ) ?: respondError(SERVER_FS_DISK_NOT_FOUND)

                respondOk("disk" to impl)
            }

            get<ServersRoute.Server.FileSystemDiskFiles> { (disk, parent) ->
                val impl = ws.katan.fs.getDisk(
                    parent.server,
                    disk
                )
                    ?: respondError(SERVER_FS_DISK_NOT_FOUND)

                respondOk(
                    "files" to impl.listFiles()
                )
            }
        }
    }
}