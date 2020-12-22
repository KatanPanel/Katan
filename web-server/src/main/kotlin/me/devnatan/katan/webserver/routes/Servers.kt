@file:OptIn(KtorExperimentalLocationsAPI::class)

package me.devnatan.katan.webserver.routes

import io.ktor.locations.*

@Location("/servers")
class ServersRoute {

    @Location("{server}")
    data class Server(val server: me.devnatan.katan.api.server.Server, val parent: ServersRoute)

}