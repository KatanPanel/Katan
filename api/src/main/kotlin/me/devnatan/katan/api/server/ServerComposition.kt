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

    }

    open class BaseKey<T : ServerComposition<*>>(override val name: String) : Key<T> {

        override fun equals(other: Any?): Boolean {
            return when (other) {
                is Key<*> -> other.name == name
                else -> false
            }
        }

    }

    val key: Key<*>

    var factory: ServerCompositionFactory

    var options: @UnsafeVariance T

    /**
     * Reads the values of this composition to the [server].
     */
    suspend fun read(server: Server)

    /**
     * Writes the values of this composition to the [server].
     */
    suspend fun write(server: Server)

}

abstract class AbstractServerComposition<T : ServerCompositionOptions> :
    ServerComposition<T> {

    override lateinit var options: T
    override lateinit var factory: ServerCompositionFactory

}

abstract class WeaklyTypedServerComposition :
    AbstractServerComposition<ServerCompositionOptions>()