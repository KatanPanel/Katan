package me.devnatan.katan.api.server

import me.devnatan.katan.api.annotations.UnstableKatanApi
import me.devnatan.katan.api.game.Game
import me.devnatan.katan.api.game.GameType

/**
 * Represents a server created by Katan.
 * Servers can be created, composed (using Server Compositions API) and modified dynamically.
 * Every server targets a [Game], its [gameType] and it is used as information for other things.
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
    @UnstableKatanApi
    val compositions: ServerCompositions

    /**
     * Returns the [GameType] that this server is targeting.
     */
    val gameType: GameType

    /**
     * Returns the server remote connection address.
     */
    val host: String

    /**
     * Returns the server remote connection port.
     */
    val port: Short

}