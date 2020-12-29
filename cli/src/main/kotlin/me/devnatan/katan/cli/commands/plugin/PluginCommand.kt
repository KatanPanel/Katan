package me.devnatan.katan.cli.commands.plugin

import com.github.ajalt.clikt.core.NoOpCliktCommand
import com.github.ajalt.clikt.core.context
import com.github.ajalt.clikt.core.subcommands
import me.devnatan.katan.cli.KatanCLI
import me.devnatan.katan.common.KatanTranslationKeys.CLI_ALIAS_PLUGIN
import me.devnatan.katan.common.KatanTranslationKeys.CLI_ALIAS_PLUGIN_LIST
import me.devnatan.katan.common.KatanTranslationKeys.CLI_HELP_PLUGIN

class PluginCommand(private val cli: KatanCLI) : NoOpCliktCommand(
    name = cli.translate(CLI_ALIAS_PLUGIN),
    help = cli.translate(CLI_HELP_PLUGIN),
    printHelpOnEmptyArgs = true
) {

    init {
        context {
            localization = cli.localization
        }

        subcommands(PluginListCommand(cli))
    }

    override fun aliases(): Map<String, List<String>> {
        return mapOf(
            "ls" to listOf(cli.translate(CLI_ALIAS_PLUGIN_LIST))
        )
    }

}