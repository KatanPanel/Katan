package me.devnatan.katan.cli

import com.github.ajalt.clikt.core.CliktCommand
import com.github.ajalt.clikt.core.context
import com.github.ajalt.clikt.core.subcommands
import com.github.ajalt.clikt.parameters.options.flag
import com.github.ajalt.clikt.parameters.options.option
import me.devnatan.katan.cli.commands.AccountsCommand
import me.devnatan.katan.cli.commands.ServersCommand

class KatanCommand(private val cli: KatanCLI) : CliktCommand(
    name = "katan",
    printHelpOnEmptyArgs = true,
    invokeWithoutSubcommand = true,
    allowMultipleSubcommands = true
) {

    private val version by option("-v", help = "Shows Katan current version.").flag()

    init {
        context {
            console = cli.console
        }

        subcommands(AccountsCommand(cli), ServersCommand(cli))
    }

    override fun run() {
        if (version)
            cli.showVersion()
    }

}