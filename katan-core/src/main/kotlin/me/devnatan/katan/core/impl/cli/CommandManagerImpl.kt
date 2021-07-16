package me.devnatan.katan.core.impl.cli

import kotlinx.coroutines.*
import me.devnatan.katan.api.logging.logger
import me.devnatan.katan.api.plugin.Plugin
import me.devnatan.katan.api.plugin.command.*
import org.slf4j.event.Level

class CommandManagerImpl : CommandManager, CoroutineScope by CoroutineScope(CoroutineName("Katan::CommandManager")) {

    companion object {

        private val logger = logger<CommandManager>()

    }

    private val commands: MutableMap<Plugin, MutableList<Command>> = hashMapOf()

    override fun getRegisteredCommands(): Map<Plugin, List<Command>> {
        return commands.toMap()
    }

    override fun getCommand(label: String): Command? {
        for ((plugin, commands) in commands) {
            commands.find {
                matchesCommand(label, it)
            }?.let { command ->
                return RegisteredCommand(plugin, command)
            }
        }

        return null
    }

    private fun matchesCommand(label: String, command: Command): Boolean {
        if (command.name.equals(label, true))
            return true

        for (alias in command.aliases)
            if (alias.equals(label, true))
                return true

        return false
    }

    override fun executeCommand(plugin: Plugin, command: Command, label: String, args: Array<out String>) {
        if (args.isNotEmpty()) {
            val target = args[0]
            for (subcommand in command) {
                if (matchesCommand(target, subcommand)) {
                    executeCommand(plugin, subcommand, "$label ${args.joinToString(" ")}", args.copyOfRange(1, args.size))
                    return
                }
            }
        }

        launch(NonCancellable + (command.dispatcher ?: Dispatchers.IO) + CoroutineName("Command-$label")) {
            try {
                command.execute(label, args)
            } catch (e: CommandException) {
                plugin.logger.error(e.message!!)
            } catch (e: FriendlyCommandException) {
                val log = e.message!!
                when (e.level) {
                    Level.DEBUG -> plugin.logger.debug(log)
                    Level.INFO -> plugin.logger.info(log)
                    Level.WARN -> plugin.logger.warn(log)
                    Level.ERROR -> plugin.logger.error(log)
                    Level.TRACE -> plugin.logger.trace(log)
                }
            }
        }
    }

    override fun registerCommand(plugin: Plugin, command: Command) {
        synchronized(commands) {
            commands.computeIfAbsent(plugin) {
                arrayListOf()
            }.add(command)
            logger.debug("Command ${command.name} registered to $plugin.")
        }
    }


}