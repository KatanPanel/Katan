package me.devnatan.katan.api.composition

/**
 * Represents the container of all the compositions present in a [Server].
 */
interface Compositions : Iterable<CompositionStore<*>> {

    /**
     * Returns the composition with the provided [key]
     * for this instance or `null` if no composition is found.
     */
    operator fun <T : CompositionOptions> get(key: Composition.Key): CompositionStore<T>?

    /**
     * Defines the value of a [key] for the specified [composition].
     */
    operator fun set(key: Composition.Key, composition: CompositionStore<*>)

}