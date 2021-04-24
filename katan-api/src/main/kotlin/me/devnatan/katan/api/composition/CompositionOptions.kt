package me.devnatan.katan.api.composition

import kotlinx.coroutines.CompletableDeferred

/**
 * Represents saved data from a composition, named options.
 */
interface CompositionOptions {

    /**
     * Using this composition option means that the composition is being manufactured directly by the CLI
     * and needs information from those who are executing the creation command, and not synthetically.
     */
    object CLI : CompositionOptions

}

/**
 * A packet is data used for communication between the CLI and the [CompositionFactory].
 */
sealed class CompositionPacket {

    /**
     * Represents the signal to request a value from the CLI.
     */
    class Prompt(
        val text: String,
        val defaultValue: String?,
        val job: CompletableDeferred<String>
    ) : CompositionPacket()

    /**
     * Represents the signal to send a message to the CLI.
     */
    class Message(val text: String, val error: Boolean) : CompositionPacket()

    /**
     * Represents the end of the composition generation cycle.
     */
    object Close : CompositionPacket()

}