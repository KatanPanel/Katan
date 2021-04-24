package me.devnatan.katan.api.composition

import me.devnatan.katan.api.server.Server

/**
 *
 */
interface Composition<out T : CompositionOptions> {

    companion object {

        fun Key(name: String): Key = KeyImpl(name)

    }

    interface Key {

        val name: String

    }

    /**
     * Returns the key for that composition.
     */
    val key: Key

    /**
     * Reads this composition for a specific [server],
     * defining its options throughout the life cycle of the server.
     */
    suspend fun read(server: Server, store: CompositionStore<@UnsafeVariance T>, factory: CompositionFactory) {
    }

    /**
     * Writes the values of this composition to the [server].
     */
    suspend fun write(server: Server, store: CompositionStore<@UnsafeVariance T>, factory: CompositionFactory) {
    }

}

private inline class KeyImpl(override val name: String) : Composition.Key