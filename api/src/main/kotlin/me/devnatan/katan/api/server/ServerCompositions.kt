package me.devnatan.katan.api.server

/**
 * Represents the container of all the compositions present in a [Server].
 */
interface ServerCompositions : Iterable<ServerComposition<*>> {

    /**
     * Returns the composition with the provided [key]
     * for this instance or `null` if no composition is found.
     */
    operator fun <T : ServerComposition<*>> get(key: ServerComposition.Key<T>): T?

    /**
     * Defines the value of a [key] for the specified [composition].
     */
    operator fun set(key: ServerComposition.Key<*>, composition: ServerComposition<*>)

}