package me.devnatan.katan.api.cli

import me.devnatan.katan.api.plugin.Plugin

// TODO: doc
interface CommandManager {

    fun getRegisteredCommands(): Map<Plugin, List<Command>>

    fun getCommand(label: String): Command?

    fun executeCommand(plugin: Plugin, command: Command, label: String, args: Array<out String>)

    fun registerCommand(plugin: Plugin, command: Command)

}