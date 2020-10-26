package me.devnatan.katan.cli.commands.server

import com.github.ajalt.clikt.core.CliktCommand
import me.devnatan.katan.cli.KatanCLI

class ServerListCommand(private val cli: KatanCLI) : CliktCommand(
    name = "ls",
    help = "Lists all servers"
) {

    override fun run() {
        val servers = cli.serverManager.getServerList()
        echo("Registered servers list (${servers.size}):")
        for (server in servers) {
            echo(
                "${server.name} - ${server.host}:${server.port} (${
                    server.state.toString().toLowerCase().run {
                        this[0].toUpperCase() + substring(1 until length)
                    }
                })"
            )
        }
    }

}