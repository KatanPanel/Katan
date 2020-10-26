package me.devnatan.katan.api.game

/**
 * Represents a game's Docker image and its environment settings.
 */
interface GameImage {

    /**
     * The name of the image.
     */
    val id: String

    /**
     * The default environment values for the image.
     */
    val environment: Map<String, Any>

}