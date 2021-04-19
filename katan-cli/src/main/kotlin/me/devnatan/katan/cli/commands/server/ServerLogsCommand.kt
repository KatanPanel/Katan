package me.devnatan.katan.cli.commands.server

import com.github.ajalt.clikt.core.CliktCommand
import com.github.ajalt.clikt.parameters.arguments.argument
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import me.devnatan.katan.api.server.getServerOrNull
import me.devnatan.katan.cli.KatanCLI
import me.devnatan.katan.cli.err
import me.devnatan.katan.common.KatanTranslationKeys.CLI_ALIAS_SERVER_LOGS
import me.devnatan.katan.common.KatanTranslationKeys.CLI_ARG_HELP_SERVER_NAME
import me.devnatan.katan.common.KatanTranslationKeys.CLI_ARG_LABEL_SERVER_NAME
import me.devnatan.katan.common.KatanTranslationKeys.CLI_HELP_SERVER_LOGS
import me.devnatan.katan.common.KatanTranslationKeys.CLI_SERVER_NOT_FOUND

class ServerLogsCommand(private val cli: KatanCLI) : CliktCommand(
    name = cli.translate(CLI_ALIAS_SERVER_LOGS),
    help = cli.translate(CLI_HELP_SERVER_LOGS)
) {

    private val serverName by argument(
        cli.translate(CLI_ARG_LABEL_SERVER_NAME),
        cli.translate(CLI_ARG_HELP_SERVER_NAME)
    )

    @OptIn(ExperimentalCoroutinesApi::class)
    override fun run() {
        val server = cli.serverManager.getServerOrNull(serverName)
            ?: return err(cli.translate(CLI_SERVER_NOT_FOUND, serverName))

        cli.coroutineScope.launch(Dispatchers.IO) {
            cli.serverManager.receiveServerLogs(server).collect { log ->
                println(log)
            }
        }
    }

}