package me.devnatan.katan.api.server

import kotlinx.coroutines.CompletableDeferred
import me.devnatan.katan.api.annotations.InternalKatanApi
import me.devnatan.katan.api.annotations.UnstableKatanApi

@InternalKatanApi
sealed class ServerCompositionPacket {

    class Prompt(
        val text: String,
        val defaultValue: String?,
        val job: CompletableDeferred<String>
    ) : ServerCompositionPacket()

    class Message(val text: String, val error: Boolean) : ServerCompositionPacket()

    object Close : ServerCompositionPacket()

}

interface ServerCompositionOptions {

    @UnstableKatanApi
    object CLI : ServerCompositionOptions

}