package me.devnatan.katan.api.server

import kotlinx.coroutines.CompletableDeferred
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.channels.Channel

sealed class Packet {

    class Prompt(val text: String, val job: CompletableDeferred<String>) : Packet()

    class Message(val content: String) : Packet()

}

interface ServerCompositionOptions {

    abstract class Adapter {

        val channel = Channel<Packet>(Channel.CONFLATED)

        abstract suspend fun apply(key: ServerComposition.Key<*>): ServerCompositionOptions

    }

}

@OptIn(ExperimentalCoroutinesApi::class)
suspend inline fun ServerCompositionOptions.Adapter.prompt(
    text: String,
    noinline block: suspend ServerCompositionOptions.Adapter.() -> Unit = {}
): String {
    val job = CompletableDeferred<String>()
    val packet = Packet.Prompt(text, job)
    block()
    channel.send(packet)
    return job.await()
}

@OptIn(ExperimentalCoroutinesApi::class)
suspend fun ServerCompositionOptions.Adapter.message(message: String) {
    channel.send(Packet.Message(message))
}