package me.devnatan.katan.api.game

/**
 * Represents the default settings for a game.
 */
interface GameSettings {

    /**
     * Returns the default Docker image for building a server.
     */
    val image: GameImage

    /**
     * Returns the minimum and maximum values for the port on a server.
     */
    val ports: IntRange

}