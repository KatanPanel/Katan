package me.devnatan.katan.cli.commands.server

import com.github.ajalt.clikt.core.CliktCommand
import com.github.ajalt.clikt.parameters.arguments.argument
import kotlinx.coroutines.launch
import me.devnatan.katan.api.server.isInactive
import me.devnatan.katan.cli.KatanCLI

class ServerStartCommand(private val cli: KatanCLI) : CliktCommand(
    name = "start",
    help = "Starts a server"
) {

    private val serverName by argument("name", "Server name")

    override fun run() {
        try {
            val server = cli.serverManager.getServer(serverName)
            if (server.state.isInactive()) {
                echo("The server is not running, use the status command to learn more.")
                return
            }

            echo("Starting server \"${server.name}\"...")
            cli.coroutineScope.launch(cli.coroutineExecutor) {
                cli.serverManager.startServer(server)
            }
        } catch (e: NoSuchElementException) {
            echo("Server $serverName not found.")
        }
    }

}