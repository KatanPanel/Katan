package me.devnatan.katan.api.server

import me.devnatan.katan.api.game.GameType
import me.devnatan.katan.api.game.GameVersion

/**
 * Represents the [Game] information that a [Server] is currently using.
 */
interface ServerGame {

    /**
     * Returns the type of the game.
     */
    val type: GameType

    /**
     * Returns the [GameVersion] being used or null if using the [Game]'s default.
     */
    val version: GameVersion?

}