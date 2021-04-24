package me.devnatan.katan.api.game

/**
 * @author Natan Vieira
 * @since  1.0
 */
interface GameVersion {

    val id: String

    /**
     * Returns the version name.
     */
    val name: String

    /**
     * Returns the version exhibition name.
     */
    val displayName: String?

    /**
     * Returns the default Docker image for building a server or `null` if not defined.
     */
    val image: String?

    /**
     * Default environment variables values for the [image].
     */
    val environment: Map<String, Any>

}