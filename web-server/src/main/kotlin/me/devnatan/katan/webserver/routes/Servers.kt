@file:OptIn(KtorExperimentalLocationsAPI::class)

package me.devnatan.katan.webserver.routes

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
        data class FS(val parent: Server)

    }

}