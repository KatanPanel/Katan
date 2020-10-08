package me.devnatan.katan.cli.commands.plugin

import com.github.ajalt.clikt.core.NoOpCliktCommand
import com.github.ajalt.clikt.core.subcommands
import me.devnatan.katan.cli.KatanCLI

class PluginCommand(cli: KatanCLI) : NoOpCliktCommand(
    name = "plugin",
    help = "List, load, unload, start and stop plugins.",
    printHelpOnEmptyArgs = true
) {

    init {
        subcommands(PluginListCommand(cli))
    }

}