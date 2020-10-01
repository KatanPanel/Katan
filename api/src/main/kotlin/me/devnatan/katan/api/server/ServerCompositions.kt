package me.devnatan.katan.api.server

interface ServerCompositions {

    /**
     * Returns the composition with the given [key] from this instance or `null`.
     */
    operator fun <T : ServerComposition<*>> get(key: ServerComposition.Key<T>): T?

    fun add(composition: ServerComposition<*>)

}