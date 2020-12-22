package me.devnatan.katan.api.server

import me.devnatan.katan.api.game.Game

/**
 * Represents a server created by Katan, servers can be
 * composed (using Server Compositions API) and modified dynamically.
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
     * Returns the server's temporarily metadata values.
     */
    val metadata: MutableMap<String, Any>

    /**
     * Returns the current server state.
     */
    var state: ServerState

    /**
     * Returns the server compositions container.
     */
    val compositions: ServerCompositions

    /**
     * Returns the [Game] that this server is targeting.
     */
    val game: ServerGame

    /**
     * Returns the server remote connection address.
     */
    val host: String

    /**
     * Returns the server remote connection port.
     */
    val port: Short

}