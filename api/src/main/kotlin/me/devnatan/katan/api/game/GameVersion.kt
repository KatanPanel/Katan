package me.devnatan.katan.api.game

/**
 * Represents a version, derivation of a [Game].
 */
interface GameVersion {

    /**
     * Returns the version name.
     */
    val name: String

    /**
     * Returns the default Docker image for building a server or `null` if not defined.
     */
    val image: String?

    /**
     * The default environment values for the image.
     */
    val environment: Map<String, Any>

}