package me.devnatan.katan.cli.commands.server

import com.github.ajalt.clikt.core.NoOpCliktCommand
import com.github.ajalt.clikt.core.subcommands
import me.devnatan.katan.cli.KatanCLI

class ServerCommand(cli: KatanCLI) : NoOpCliktCommand(
    name = "server",
    help = "Create, remove and manage servers",
    printHelpOnEmptyArgs = true
) {

    init {
        subcommands(
            ServerListCommand(cli),
            ServerCreateCommand(cli),
            ServerStartCommand(cli),
            ServerStopCommand(cli),
            ServerInfoCommand(cli)
        )
    }

}