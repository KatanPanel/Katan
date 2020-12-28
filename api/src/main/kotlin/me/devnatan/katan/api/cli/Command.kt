package me.devnatan.katan.api.cli

import kotlinx.coroutines.CoroutineDispatcher
import org.slf4j.event.Level

// TODO: doc
interface Command : Iterable<Command> {

    val name: String

    val aliases: List<String>

    val subcommands: List<Command>

    val dispatcher: CoroutineDispatcher?

    fun addCommand(command: Command)

    fun removeCommand(command: Command)

    suspend fun execute(label: String, args: Array<out String>)

}

fun Command.log(message: String, level: Level = Level.INFO) {
    throw FriendlyCommandException(message, level)
}

fun Command.fail(message: String, cause: Throwable? = null): Nothing {
    throw CommandException(message, cause)
}