package me.devnatan.katan.api.command

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