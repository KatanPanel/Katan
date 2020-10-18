package me.devnatan.katan.api.server

import me.devnatan.katan.api.annotations.UnstableKatanApi

/**
 * A server is based on a [container], can be turned on, off and composed.
 * Every server targets a game, its [target] and it is used as information for other things.
 */
interface Server {

    /**
     * Returns the server id.
     */
    val id: Int

    /**
     * Returns the server name.
     */
    var name: String

    /**
     * Returns all accounts that have permissions on that server.
     */
    val holders: MutableSet<ServerHolder>

    /**
     * Returns the [ServerContainer] linked to this server.
     */
    val container: ServerContainer

    /**
     * Returns the remote server address search results.
     */
    val query: ServerQuery

    /**
     * Returns the current server state.
     */
    var state: ServerState

    /**
     * Returns all server compositions.
     */
    @UnstableKatanApi
    val compositions: ServerCompositions

    /**
     * Returns the game that this server is targeting.
     */
    val target: ServerTarget

}