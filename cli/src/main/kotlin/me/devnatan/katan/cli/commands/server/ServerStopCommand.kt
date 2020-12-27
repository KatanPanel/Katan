package me.devnatan.katan.cli.commands.server

import com.github.ajalt.clikt.core.CliktCommand
import com.github.ajalt.clikt.parameters.arguments.argument
import com.github.ajalt.clikt.parameters.options.default
import com.github.ajalt.clikt.parameters.options.option
import com.github.ajalt.clikt.parameters.types.long
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import me.devnatan.katan.api.server.getServerOrNull
import me.devnatan.katan.api.server.isActive
import me.devnatan.katan.cli.KatanCLI
import me.devnatan.katan.cli.err
import me.devnatan.katan.common.KatanTranslationKeys.CLI_ALIAS_SERVER_STOP
import me.devnatan.katan.common.KatanTranslationKeys.CLI_ARG_HELP_SERVER_NAME
import me.devnatan.katan.common.KatanTranslationKeys.CLI_ARG_LABEL_SERVER_NAME
import me.devnatan.katan.common.KatanTranslationKeys.CLI_HELP_SERVER_STOP
import me.devnatan.katan.common.KatanTranslationKeys.CLI_OPTION_SERVER_STOP_TIMEOUT
import me.devnatan.katan.common.KatanTranslationKeys.CLI_SERVER_NOT_FOUND
import me.devnatan.katan.common.KatanTranslationKeys.CLI_SERVER_NOT_RUNNING
import java.time.Duration

class ServerStopCommand(private val cli: KatanCLI) : CliktCommand(
    name = cli.translate(CLI_ALIAS_SERVER_STOP),
    help = cli.translate(CLI_HELP_SERVER_STOP)
) {

    private val serverName by argument(
        cli.translate(CLI_ARG_LABEL_SERVER_NAME),
        cli.translate(CLI_ARG_HELP_SERVER_NAME)
    )

    private val timeout by option("-t", help = cli.translate(CLI_OPTION_SERVER_STOP_TIMEOUT)).long().default(10)

    override fun run() {
        val server = cli.serverManager.getServerOrNull(serverName)
            ?: return err(cli.translate(CLI_SERVER_NOT_FOUND, serverName))

        if (!server.state.isActive())
            return err(cli.translate(CLI_SERVER_NOT_RUNNING, server.name))

        cli.coroutineScope.launch(Dispatchers.IO) {
            cli.serverManager.stopServer(server, Duration.ofSeconds(timeout))
        }
    }

}