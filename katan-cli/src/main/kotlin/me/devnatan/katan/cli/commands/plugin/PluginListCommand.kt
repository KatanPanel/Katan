package me.devnatan.katan.cli.commands.plugin

import com.github.ajalt.clikt.core.CliktCommand
import com.jakewharton.picnic.TextAlignment
import com.jakewharton.picnic.table
import me.devnatan.katan.cli.KatanCLI
import me.devnatan.katan.cli.render
import me.devnatan.katan.common.KatanTranslationKeys.CLI_ALIAS_PLUGIN_LIST
import me.devnatan.katan.common.KatanTranslationKeys.CLI_HELP_PLUGIN_LIST
import me.devnatan.katan.common.KatanTranslationKeys.CLI_PLUGIN_LIST
import me.devnatan.katan.common.KatanTranslationKeys.CLI_PLUGIN_LIST_DESCRIPTOR_AUTHOR
import me.devnatan.katan.common.KatanTranslationKeys.CLI_PLUGIN_LIST_DESCRIPTOR_NAME
import me.devnatan.katan.common.KatanTranslationKeys.CLI_PLUGIN_LIST_DESCRIPTOR_VERSION
import me.devnatan.katan.common.KatanTranslationKeys.CLI_PLUGIN_LIST_STATE
import me.devnatan.katan.common.KatanTranslationKeys.CLI_PLUGIN_STATE
import me.devnatan.katan.common.util.timeFormatter

class PluginListCommand(private val cli: KatanCLI) : CliktCommand(
    name = cli.translate(CLI_ALIAS_PLUGIN_LIST),
    help = cli.translate(CLI_HELP_PLUGIN_LIST)
) {

    override fun run() {
        val plugins = cli.pluginManager.getPlugins()
        render(table {
            cellStyle {
                paddingLeft = 1
                paddingRight = 1
                borderLeft = true
                borderRight = true
            }

            header {
                cellStyle { alignment = TextAlignment.BottomCenter }
                row {
                    cell(cli.translate(CLI_PLUGIN_LIST, plugins.size)) {
                        columnSpan = 4
                        paddingBottom = 1
                    }
                }

                row(cli.translate(CLI_PLUGIN_LIST_DESCRIPTOR_NAME),
                    cli.translate(CLI_PLUGIN_LIST_DESCRIPTOR_VERSION),
                    cli.translate(CLI_PLUGIN_LIST_DESCRIPTOR_AUTHOR),
                    cli.translate(CLI_PLUGIN_LIST_STATE),
                )
            }

            body {
                for (plugin in plugins.sortedBy { it.state })
                    row(
                        plugin.descriptor.id,
                        plugin.descriptor.version?.toString() ?: "",
                        plugin.descriptor.author.orEmpty(),
                        cli.translate("$CLI_PLUGIN_STATE.${plugin.state.order}", "(${timeFormatter.format(plugin.state.timestamp)})")
                    )
            }
        })
    }

}