@file:OptIn(ExperimentalCoroutinesApi::class, InternalKatanAPI::class)

package me.devnatan.katan.api.server

import kotlinx.coroutines.CompletableDeferred
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.channels.BroadcastChannel
import kotlinx.coroutines.channels.Channel
import me.devnatan.katan.api.InternalKatanAPI

interface ServerCompositionFactory {

    val applicable: Array<out ServerComposition.Key<*>>

    val channel: BroadcastChannel<ServerCompositionPacket>

    /**
     * Creates a new server composition using [server] as an argument.
     * @param server the server subject to composition
     */
    suspend fun create(
        key: ServerComposition.Key<*>,
        server: Server,
        options: ServerCompositionOptions
    ): ServerComposition<*>

}

abstract class AbstractServerCompositionFactory(
    override vararg val applicable: ServerComposition.Key<*> = emptyArray()
) : ServerCompositionFactory {

    @OptIn(ExperimentalCoroutinesApi::class)
    override val channel = BroadcastChannel<ServerCompositionPacket>(Channel.CONFLATED)

}

/**
 * Creates a new factory using [factory] as a manufacturing method.
 */
fun newCompositionFactory(
    vararg forKeys: ServerComposition.Key<*> = emptyArray(),
    factory: FactoryHandler
): ServerCompositionFactory {
    return FactoryImpl(forKeys, factory)
}

private typealias FactoryHandler = (ServerComposition.Key<*>, Server, ServerCompositionOptions) -> ServerComposition<*>

private class FactoryImpl(
    override val applicable: Array<out ServerComposition.Key<*>>,
    private inline val factory: FactoryHandler
) : AbstractServerCompositionFactory(*applicable) {

    override suspend fun create(
        key: ServerComposition.Key<*>,
        server: Server,
        options: ServerCompositionOptions
    ): ServerComposition<*> {
        return factory.invoke(key, server, options)
    }

}

operator fun ServerCompositionFactory.get(keyName: String): ServerComposition.Key<*>? {
    return applicable.firstOrNull { it.name.equals(keyName, true) }
}

@OptIn(ExperimentalCoroutinesApi::class, InternalKatanAPI::class)
suspend inline fun ServerCompositionFactory.prompt(
    text: String,
    defaultValue: String? = null
): String {
    val job = CompletableDeferred<String>()
    val packet = ServerCompositionPacket.Prompt(text, defaultValue, job)
    channel.send(packet)
    return job.await()
}

@OptIn(ExperimentalCoroutinesApi::class, InternalKatanAPI::class)
suspend fun ServerCompositionFactory.message(message: String) {
    channel.send(ServerCompositionPacket.Message(message))
}
