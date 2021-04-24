@file:OptIn(KtorExperimentalLocationsAPI::class)

package me.devnatan.katan.webserver.router

import io.ktor.locations.*

@Location("/servers")
class ServersRoute {

    @Location("{server}")
    data class Server(val server: me.devnatan.katan.api.server.Server, val parent: ServersRoute) {

        @Location("start")
        data class Start(val parent: Server)

        @Location("stop")
        data class Stop(val parent: Server)

        @Location("fs")
        data class FileSystem(val parent: Server)

        @Location("fs/disks/{disk}")
        data class FileSystemDisk(val disk: String, val parent: Server)

        @Location("fs/disks/{disk}/files")
        data class FileSystemDiskFiles(val disk: String, val parent: Server)

    }

}