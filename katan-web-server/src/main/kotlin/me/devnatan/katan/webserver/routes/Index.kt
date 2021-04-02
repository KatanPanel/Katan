@file:OptIn(KtorExperimentalLocationsAPI::class)

package me.devnatan.katan.webserver.routes

import io.ktor.locations.*

@Location("/")
class IndexRoute

@Location("/info")
class InfoRoute {

    @Location("accounts")
    data class Accounts(val info: InfoRoute)

    @Location("games")
    data class Games(val info: InfoRoute)

    @Location("plugins")
    data class Plugins(val info: InfoRoute)

    @Location("permissions")
    data class Permissions(val info: InfoRoute)

}