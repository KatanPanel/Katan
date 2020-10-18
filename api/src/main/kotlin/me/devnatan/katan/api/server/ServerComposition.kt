package me.devnatan.katan.api.server

import me.devnatan.katan.api.annotations.UnstableKatanApi

/**
 * Represents a composition of a server, as if it were a part of its body.
 *
 * A server can have ~~multiple compositions~~, compositions are called when a server
 * is read and when it is written, that is, when it is created and when it is loaded.
 * Compositions are called at random, that is, it is not possible for one composition to depend on the other,
 * except in the case of [CombinedServerComposition], which you can determine its order.
 *
 * As identification, compositions have keys, determining
 * which key is which composition is a [ServerCompositionFactory] task.
 */
@UnstableKatanApi
interface ServerComposition<T : ServerCompositionOptions> {

    /**
     * Represents the key to a composition.
     */
    interface Key<T : ServerComposition<*>>

    /**
     * Returns the key for that composition.
     */
    val key: Key<*>

    /**
     * Returns the factory from which the composition originated.
     */
    val factory: ServerCompositionFactory

    /**
     * Returns the options, always available after [read], of that composition.
     */
    val options: T

    /**
     * Reads this composition for a specific [server],
     * defining its options throughout the life cycle of the server.
     */
    suspend fun read(server: Server)

    /**
     * Writes the values of this composition to the [server].
     */
    suspend fun write(server: Server)

}

/**
 * Returns a [CombinedServerComposition] of the result of joining this composition with [other].
 * If any composition is combined it will be deconstructed.
 */
@UnstableKatanApi
operator fun ServerComposition<*>.plus(other: ServerComposition<*>): ServerComposition<*> {
    return CombinedServerComposition(
        when {
            this is CombinedServerComposition -> compositions + other
            other is CombinedServerComposition -> other.compositions + this
            this is CombinedServerComposition && other is CombinedServerComposition -> compositions + other.compositions
            else -> listOf(other, this)
        }
    )
}

/**
 * Returns a composition of the compositions that are combined that contains
 * the specified [key] or `null` if the key composition has not been combined.
 */
@UnstableKatanApi
operator fun ServerComposition<*>.get(key: ServerComposition.Key<*>): ServerComposition<*>? {
    if (this !is CombinedServerComposition)
        throw UnsupportedOperationException("Only combined compositions have get operators")

    return compositions.find { it.key == key }
}

/**
 * A combination of compositions, created to serve as a "module" for compositions.
 * Combined compositions have indexing ([get]) and addition ([plus]) method.
 *
 * Combined compositions do not have properties as they use their own,
 * so it is not possible to access the combined [key], [factory] and [options].
 */
@UnstableKatanApi
class CombinedServerComposition(
    val compositions: List<ServerComposition<*>>
) : ServerComposition<ServerCompositionOptions> {

    override val key: ServerComposition.Key<*> get() = unsupported()
    override val factory: ServerCompositionFactory get() = unsupported()
    override val options: ServerCompositionOptions get() = unsupported()

    override suspend fun read(server: Server) {
        for (composition in compositions)
            composition.read(server)
    }

    override suspend fun write(server: Server) {
        for (composition in compositions)
            composition.write(server)
    }

    private fun unsupported(): Nothing {
        throw UnsupportedOperationException("Combined compositions cannot be accessed directly")
    }

}