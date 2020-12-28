package me.devnatan.katan.api.cli

import kotlinx.coroutines.CoroutineDispatcher

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

fun Command.fail(message: String, cause: Throwable? = null): Nothing {
    throw CommandException(message, cause)
}

fun Command.addCommands(vararg commands: Command) {
    for (command in commands)
        addCommand(command)
}

abstract class KatanCommand(
    override val name: String,
    override val aliases: List<String> = emptyList()
) : Command {

    final override val subcommands: MutableList<Command> = arrayListOf()
    override val dispatcher: CoroutineDispatcher? = null

    final override fun addCommand(command: Command) {
        synchronized(subcommands) {
            subcommands.add(command)
        }
    }

    final override fun removeCommand(command: Command) {
        synchronized(subcommands) {
            subcommands.remove(command)
        }
    }

    final override fun iterator(): Iterator<Command> {
        return subcommands.iterator()
    }

}