package me.devnatan.katan.api.server

interface ServerCompositionFactory {

    val lazy: Boolean

    val applicable: Array<out ServerComposition.Key<*>>

    val adapter: ServerCompositionOptions.Adapter

    /**
     * Creates a new server composition using [server] as an argument.
     * @param server the server subject to composition
     */
    fun create(key: ServerComposition.Key<*>, server: Server): ServerComposition<*>

}

/**
 * Creates a new factory using [factory] as a manufacturing method.
 */
fun newCompositionFactory(
    adapter: ServerCompositionOptions.Adapter,
    vararg forKeys: ServerComposition.Key<*> = emptyArray(),
    factory: FactoryHandler
): ServerCompositionFactory {
    return FactoryImpl(false, adapter, forKeys, factory)
}

/**
 * Creates a new factory using [factory] as a manufacturing method.
 */
fun newLazyCompositionFactory(
    adapter: ServerCompositionOptions.Adapter,
    vararg forKeys: ServerComposition.Key<*> = emptyArray(),
    factory: FactoryHandler
): ServerCompositionFactory {
    return FactoryImpl(true, adapter, forKeys, factory)
}

private typealias FactoryHandler = (ServerComposition.Key<*>, Server) -> ServerComposition<*>

private class FactoryImpl(
    override val lazy: Boolean,
    override val adapter: ServerCompositionOptions.Adapter,
    override val applicable: Array<out ServerComposition.Key<*>>,
    private inline val factory: FactoryHandler
) : ServerCompositionFactory {

    override fun create(key: ServerComposition.Key<*>, server: Server): ServerComposition<*> {
        return factory.invoke(key, server)
    }

}

operator fun ServerCompositionFactory.get(keyName: String): ServerComposition.Key<*>? {
    return applicable.firstOrNull { it.name.equals(keyName, true) }
}