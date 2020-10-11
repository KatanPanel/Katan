package me.devnatan.katan.api.server

/**
 * Represents the type of composition of a [Server], it is used at the time of its creation.
 * Influence directly forms that the server will be created.
 */
interface ServerComposition<T : ServerCompositionOptions> {

    interface Key<T : ServerComposition<*>>

    val key: Key<*>

    val factory: ServerCompositionFactory

    val options: T

    /**
     * Reads the values of this composition to the [server].
     */
    suspend fun read(server: Server)

    /**
     * Writes the values of this composition to the [server].
     */
    suspend fun write(server: Server)

}

class CombinedServerComposition<T : ServerCompositionOptions>(
    override val key: ServerComposition.Key<CombinedServerComposition<T>>,
    override var factory: ServerCompositionFactory,
    override var options: T,
    val compositions: Array<out ServerComposition<T>>
) : ServerComposition<T> {

    override suspend fun read(server: Server) {
        for (composition in compositions)
            composition.read(server)
    }

    override suspend fun write(server: Server) {
        for (composition in compositions)
            composition.write(server)
    }

}