package me.devnatan.katan.cli.commands.server

import com.github.ajalt.clikt.core.NoOpCliktCommand
import com.github.ajalt.clikt.core.subcommands
import me.devnatan.katan.cli.KatanCLI
import me.devnatan.katan.common.KatanTranslationKeys.CLI_ALIAS_SERVER
import me.devnatan.katan.common.KatanTranslationKeys.CLI_ALIAS_SERVER_CREATE
import me.devnatan.katan.common.KatanTranslationKeys.CLI_ALIAS_SERVER_INFO
import me.devnatan.katan.common.KatanTranslationKeys.CLI_ALIAS_SERVER_LIST
import me.devnatan.katan.common.KatanTranslationKeys.CLI_ALIAS_SERVER_LOGS
import me.devnatan.katan.common.KatanTranslationKeys.CLI_ALIAS_SERVER_START
import me.devnatan.katan.common.KatanTranslationKeys.CLI_ALIAS_SERVER_STATS
import me.devnatan.katan.common.KatanTranslationKeys.CLI_ALIAS_SERVER_STOP
import me.devnatan.katan.common.KatanTranslationKeys.CLI_HELP_SERVER

class ServerCommand(private val cli: KatanCLI) : NoOpCliktCommand(
    name = cli.translate(CLI_ALIAS_SERVER),
    help = cli.translate(CLI_HELP_SERVER),
    printHelpOnEmptyArgs = true
) {

    override fun aliases(): Map<String, List<String>> {
        return mapOf(
            "create" to listOf(cli.translate(CLI_ALIAS_SERVER_CREATE)),
            "start" to listOf(cli.translate(CLI_ALIAS_SERVER_START)),
            "stop" to listOf(cli.translate(CLI_ALIAS_SERVER_STOP)),
            "info" to listOf(cli.translate(CLI_ALIAS_SERVER_INFO)),
            "logs" to listOf(cli.translate(CLI_ALIAS_SERVER_LOGS)),
            "stats" to listOf(cli.translate(CLI_ALIAS_SERVER_STATS)),
            "ls" to listOf(cli.translate(CLI_ALIAS_SERVER_LIST))
        )
    }

    init {
        subcommands(
            ServerListCommand(cli),
            ServerCreateCommand(cli),
            ServerStartCommand(cli),
            ServerStopCommand(cli),
            ServerInfoCommand(cli),
            ServerStatsCommand(cli),
            ServerLogsCommand(cli)
        )
    }

}