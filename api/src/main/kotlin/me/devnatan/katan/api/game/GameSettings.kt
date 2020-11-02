package me.devnatan.katan.api.game

/**
 * Represents the settings for a [Game].
 */
interface GameSettings {

    /**
     * Returns the minimum and maximum values for the port on a server.
     */
    val ports: IntRange

}