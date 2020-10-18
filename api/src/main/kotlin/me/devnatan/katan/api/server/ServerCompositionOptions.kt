package me.devnatan.katan.api.server

import kotlinx.coroutines.CompletableDeferred
import me.devnatan.katan.api.annotations.UnstableKatanApi

/**
 * Represents saved data from a composition, named options.
 */
@UnstableKatanApi
interface ServerCompositionOptions {

    /**
     * Using this composition means that the composition is being manufactured directly by the CLI
     * and needs information from those who are executing the creation command, and not synthetically.
     */
    object CLI : ServerCompositionOptions

}

/**
 * A packet is data used for communication between the CLI and the [ServerCompositionFactory].
 */
@UnstableKatanApi
sealed class ServerCompositionPacket {

    /**
     * Represents the signal to request a value from the CLI.
     */
    class Prompt(
        val text: String,
        val defaultValue: String?,
        val job: CompletableDeferred<String>
    ) : ServerCompositionPacket()

    /**
     * Represents the signal to send a message to the CLI.
     */
    class Message(val text: String, val error: Boolean) : ServerCompositionPacket()

    /**
     * Represents the end of the composition generation cycle.
     */
    object Close : ServerCompositionPacket()

}