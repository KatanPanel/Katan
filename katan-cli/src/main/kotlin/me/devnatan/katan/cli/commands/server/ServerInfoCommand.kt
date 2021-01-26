package me.devnatan.katan.cli.commands.server

import com.github.ajalt.clikt.core.CliktCommand
import com.github.ajalt.clikt.parameters.arguments.argument
import me.devnatan.katan.api.server.getServerOrNull
import me.devnatan.katan.cli.KatanCLI
import me.devnatan.katan.cli.err
import me.devnatan.katan.common.KatanTranslationKeys.CLI_ALIAS_SERVER_INFO
import me.devnatan.katan.common.KatanTranslationKeys.CLI_ARG_HELP_SERVER_NAME
import me.devnatan.katan.common.KatanTranslationKeys.CLI_ARG_LABEL_SERVER_NAME
import me.devnatan.katan.common.KatanTranslationKeys.CLI_HELP_SERVER_INFO
import me.devnatan.katan.common.KatanTranslationKeys.CLI_SERVER_INFO_COMPOSITIONS
import me.devnatan.katan.common.KatanTranslationKeys.CLI_SERVER_INFO_CONTAINER_ID
import me.devnatan.katan.common.KatanTranslationKeys.CLI_SERVER_INFO_GAME
import me.devnatan.katan.common.KatanTranslationKeys.CLI_SERVER_INFO_HOST
import me.devnatan.katan.common.KatanTranslationKeys.CLI_SERVER_INFO_ID
import me.devnatan.katan.common.KatanTranslationKeys.CLI_SERVER_INFO_METADATA
import me.devnatan.katan.common.KatanTranslationKeys.CLI_SERVER_INFO_NAME
import me.devnatan.katan.common.KatanTranslationKeys.CLI_SERVER_INFO_STATE
import me.devnatan.katan.common.KatanTranslationKeys.CLI_SERVER_NOT_FOUND
import me.devnatan.katan.common.KatanTranslationKeys.CLI_SERVER_STATE

class ServerInfoCommand(private val cli: KatanCLI) : CliktCommand(
    name = cli.translate(CLI_ALIAS_SERVER_INFO),
    help = cli.translate(CLI_HELP_SERVER_INFO)
) {

    private val serverName by argument(
        cli.translate(CLI_ARG_LABEL_SERVER_NAME),
        cli.translate(CLI_ARG_HELP_SERVER_NAME)
    )

    override fun run() {
        val server = cli.serverManager.getServerOrNull(serverName)
            ?: return err(cli.translate(CLI_SERVER_NOT_FOUND, serverName))


        val output = mutableListOf(
            "${cli.translate(CLI_SERVER_INFO_ID)}: ${server.id}",
            "${cli.translate(CLI_SERVER_INFO_CONTAINER_ID)}: ${server.container}",
            "${cli.translate(CLI_SERVER_INFO_NAME)}: ${server.name}",
            "${cli.translate(CLI_SERVER_INFO_HOST)}: ${server.host}:${server.port}",
            "${cli.translate(CLI_SERVER_INFO_STATE)}: ${cli.translate("${CLI_SERVER_STATE}.${server.state.name.toLowerCase()}")}",
            "${cli.translate(CLI_SERVER_INFO_GAME)}: ${server.game.type.name} ${server.game.version?.let {
                "(${it.name})"
            }}"
        )

        output.add(cli.translate(CLI_SERVER_INFO_METADATA, server.metadata.size) + ":")
        for ((k, v) in server.metadata)
            output.add("  - $k: $v")

        output.add(cli.translate(CLI_SERVER_INFO_COMPOSITIONS) + ":")
        for (composition in server.compositions)
            output.add("  - ${composition.key}")

        output.forEach(::echo)
    }

}