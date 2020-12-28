package me.devnatan.katan.cli

import com.github.ajalt.clikt.core.CliktCommand
import com.github.ajalt.clikt.core.NoOpCliktCommand
import com.github.ajalt.clikt.core.context
import com.github.ajalt.clikt.core.subcommands
import com.github.ajalt.clikt.output.TermUi
import com.jakewharton.picnic.Table
import com.jakewharton.picnic.renderText
import me.devnatan.katan.cli.commands.VersionCommand
import me.devnatan.katan.cli.commands.account.AccountCommand
import me.devnatan.katan.cli.commands.plugin.PluginCommand
import me.devnatan.katan.cli.commands.server.ServerCommand
import me.devnatan.katan.common.KatanTranslationKeys.CLI_ALIAS_ACCOUNT
import me.devnatan.katan.common.KatanTranslationKeys.CLI_ALIAS_PLUGIN
import me.devnatan.katan.common.KatanTranslationKeys.CLI_ALIAS_SERVER
import me.devnatan.katan.common.KatanTranslationKeys.CLI_ALIAS_VERSION

class KatanCommand(private val cli: KatanCLI) : NoOpCliktCommand(
    name = KatanCLI.KATAN_COMMAND,
    printHelpOnEmptyArgs = true,
    invokeWithoutSubcommand = true,
    allowMultipleSubcommands = true
) {

    init {
        context {
            console = cli.console
            localization = KatanLocalization(cli.katan.translator)
        }

        subcommands(
            VersionCommand(cli),
            AccountCommand(cli),
            ServerCommand(cli),
            PluginCommand(cli)
        )
    }

    override fun aliases(): Map<String, List<String>> {
        return mapOf(
            "version" to listOf(cli.translate(CLI_ALIAS_VERSION)),
            "account" to listOf(cli.translate(CLI_ALIAS_ACCOUNT)),
            "server" to listOf(cli.translate(CLI_ALIAS_SERVER)),
            "plugin" to listOf(cli.translate(CLI_ALIAS_PLUGIN))
        )
    }

}

fun CliktCommand.err(vararg messages: Any?) {
    for (message in messages) {
        if (message is Iterable<*>) {
            err(message)
            continue
        }

        TermUi.echo(
            message,
            trailingNewline = true,
            err = true,
            currentContext.console,
            currentContext.console.lineSeparator
        )
    }
}

fun CliktCommand.render(table: Table) {
    table.renderText().split(System.lineSeparator()).forEach {
        TermUi.echo(it, trailingNewline = true,
            err = false,
            currentContext.console,
            currentContext.console.lineSeparator) }
}