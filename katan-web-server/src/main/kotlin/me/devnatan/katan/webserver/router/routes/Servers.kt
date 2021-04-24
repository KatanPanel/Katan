package me.devnatan.katan.webserver.router.routes

import io.ktor.application.*
import io.ktor.http.*
import io.ktor.locations.*
import io.ktor.response.*
import io.ktor.routing.*
import me.devnatan.katan.webserver.KatanWS
import me.devnatan.katan.webserver.SERVER_FS_DISK_NOT_FOUND
import me.devnatan.katan.webserver.router.ServersRoute
import me.devnatan.katan.webserver.util.respondError
import me.devnatan.katan.webserver.util.respondOk
import java.time.Duration

@KtorExperimentalLocationsAPI
fun Route.serversRoutes(ws: KatanWS) {
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

    serversFileSystemRoutes(ws)
}

fun Route.serversFileSystemRoutes(ws: KatanWS) {
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