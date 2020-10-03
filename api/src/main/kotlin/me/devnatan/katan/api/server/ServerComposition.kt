package me.devnatan.katan.api.server

/**
 * Represents the type of composition of a [Server], it is used at the time of its creation.
 * Influence directly forms that the server will be created.
 */
interface ServerComposition<out T : ServerCompositionOptions> {

    interface Key<T : ServerComposition<*>> {

        /**
         * The name of this composition.
         *
         * This field is used for identification and external services (like plugins)
         * are able to register compositions, so try to use a value that you know will not conflict.
         */
        val name: String

        val single: Boolean

        val default: Boolean

    }

    val key: Key<*>

    val factory: ServerCompositionFactory

    val options: @UnsafeVariance T

    /**
     * Reads the values of this composition to the [server].
     */
    suspend fun read(server: Server)

    /**
     * Writes the values of this composition to the [server].
     */
    suspend fun write(server: Server)

}

fun <T : ServerComposition<*>> createCompositionKey(
    name: String,
    single: Boolean = false,
    default: Boolean = false
): ServerComposition.Key<T> {
    return object : ServerComposition.Key<T> {
        override val name: String = name
        override val single: Boolean = single
        override val default: Boolean = default
    }
}