package me.devnatan.katan.api.server

import me.devnatan.katan.api.annotations.UnstableKatanApi

/**
 * The container of all the compositions present in a server.
 * @see Server.compositions
 */
@UnstableKatanApi
interface ServerCompositions : Iterable<ServerComposition<*>> {

    /**
     * Returns the composition with the provided [key]
     * for this instance or `null` if no composition is found.
     */
    operator fun <T : ServerComposition<*>> get(key: ServerComposition.Key<T>): T?

}