package me.devnatan.katan.cli.commands.plugin

import com.github.ajalt.clikt.core.NoOpCliktCommand
import com.github.ajalt.clikt.core.subcommands
import me.devnatan.katan.cli.KatanCLI

class PluginCommand(cli: KatanCLI) : NoOpCliktCommand(
    name = "plugin",
    help = cli.katan.translator.translate("cli.help.plugin"),
    printHelpOnEmptyArgs = true
) {

    init {
        subcommands(PluginListCommand(cli))
    }

}