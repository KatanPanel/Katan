package me.devnatan.katan.api.server

import kotlinx.coroutines.CompletableDeferred
import me.devnatan.katan.api.InternalKatanAPI

@InternalKatanAPI
sealed class ServerCompositionPacket {

    class Prompt(
        val text: String,
        val defaultValue: String?,
        val job: CompletableDeferred<String>
    ) : ServerCompositionPacket()

    class Message(val content: String) : ServerCompositionPacket()

}

interface ServerCompositionOptions {

    @InternalKatanAPI
    object CLI : ServerCompositionOptions

}