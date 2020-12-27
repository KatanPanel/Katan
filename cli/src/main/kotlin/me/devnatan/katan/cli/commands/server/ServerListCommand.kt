package me.devnatan.katan.cli.commands.server

import com.github.ajalt.clikt.core.CliktCommand
import com.jakewharton.picnic.TextAlignment
import com.jakewharton.picnic.table
import me.devnatan.katan.cli.KatanCLI
import me.devnatan.katan.cli.render
import me.devnatan.katan.common.KatanTranslationKeys.CLI_ALIAS_SERVER_LIST
import me.devnatan.katan.common.KatanTranslationKeys.CLI_HELP_SERVER_LIST
import me.devnatan.katan.common.KatanTranslationKeys.CLI_SERVER_LIST
import me.devnatan.katan.common.KatanTranslationKeys.CLI_SERVER_LIST_GAME
import me.devnatan.katan.common.KatanTranslationKeys.CLI_SERVER_LIST_HOST
import me.devnatan.katan.common.KatanTranslationKeys.CLI_SERVER_LIST_ID
import me.devnatan.katan.common.KatanTranslationKeys.CLI_SERVER_LIST_NAME
import me.devnatan.katan.common.KatanTranslationKeys.CLI_SERVER_LIST_STATE
import me.devnatan.katan.common.KatanTranslationKeys.CLI_SERVER_STATE

class ServerListCommand(private val cli: KatanCLI) : CliktCommand(
    name = cli.translate(CLI_ALIAS_SERVER_LIST),
    help = cli.translate(CLI_HELP_SERVER_LIST)
) {

    override fun run() {
        val servers = cli.serverManager.getServerList()
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
                    cell(cli.translate(CLI_SERVER_LIST, servers.size)) {
                        columnSpan = 5
                        paddingBottom = 1
                    }
                }

                row(cli.translate(CLI_SERVER_LIST_ID),
                    cli.translate(CLI_SERVER_LIST_NAME),
                    cli.translate(CLI_SERVER_LIST_HOST),
                    cli.translate(CLI_SERVER_LIST_STATE),
                    cli.translate(CLI_SERVER_LIST_GAME)
                )
            }

            body {
                for (server in servers.sortedBy { it.id }) {
                    row(server.id, server.name, "${server.host}:${server.port}", cli.translate("$CLI_SERVER_STATE.${server.state.name.toLowerCase()}"), server.game)
                }
            }
        })
    }

}