package me.devnatan.katan.cli

import com.github.ajalt.clikt.core.CliktCommand
import com.github.ajalt.clikt.core.NoOpCliktCommand
import com.github.ajalt.clikt.core.context
import com.github.ajalt.clikt.core.subcommands
import com.github.ajalt.clikt.output.TermUi
import me.devnatan.katan.api.Katan
import me.devnatan.katan.cli.commands.account.AccountCommand
import me.devnatan.katan.cli.commands.compose.ComposeCommand
import me.devnatan.katan.cli.commands.plugin.PluginCommand
import me.devnatan.katan.cli.commands.server.ServerCommand

class KatanCommand(cli: KatanCLI) : NoOpCliktCommand(
    name = "katan",
    printHelpOnEmptyArgs = true,
    invokeWithoutSubcommand = true,
    allowMultipleSubcommands = true
) {

    init {
        context {
            console = cli.console
        }

        subcommands(
            VersionCommand(cli),
            AccountCommand(cli),
            ServerCommand(cli),
            PluginCommand(cli),
            ComposeCommand(cli)
        )
    }

}

class VersionCommand(private val cli: KatanCLI) : CliktCommand(
    name = "version",
    help = "Shows the current version of Katan and its platform."
) {

    override fun run() {
        echo("Running on Katan v${Katan.VERSION}.")
        echo(
            "Platform: ${
                cli.katan.platform.run {
                    "${os.name} ${os.version} (${os.arch})"
                }
            }"
        )
        echo("Environment: ${cli.katan.environment}")
    }

}

fun CliktCommand.err(vararg messages: Any?) {
    for (message in messages) {
        if (message is Iterable<*>)
            for (value in message) err(value)
        else
            TermUi.echo(
                message,
                trailingNewline = true,
                err = true,
                currentContext.console,
                currentContext.console.lineSeparator
            )
    }
}