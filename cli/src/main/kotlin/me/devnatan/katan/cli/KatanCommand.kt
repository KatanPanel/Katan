package me.devnatan.katan.cli

import com.github.ajalt.clikt.core.CliktCommand
import com.github.ajalt.clikt.core.context
import com.github.ajalt.clikt.core.subcommands
import com.github.ajalt.clikt.parameters.options.flag
import com.github.ajalt.clikt.parameters.options.option
import me.devnatan.katan.cli.commands.AccountsCommand

class KatanCommand(cli: KatanCLI) : CliktCommand(
    name = "katan",
    printHelpOnEmptyArgs = true,
    invokeWithoutSubcommand = true,
    allowMultipleSubcommands = true
) {

    private val version by option("-v", help = "Shows Katan current version.").flag()

    init {
        context {
            console = KatanCLI.Console
        }

        subcommands(AccountsCommand(cli))
    }

    override fun run() {
        if (version)
            KatanCLI.showVersion()
    }

}