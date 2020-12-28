package me.devnatan.katan.api.cli

import kotlinx.coroutines.CoroutineDispatcher

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