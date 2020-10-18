package me.devnatan.katan.cli.commands.server

import com.github.ajalt.clikt.core.CliktCommand
import com.github.ajalt.clikt.parameters.arguments.argument
import kotlinx.coroutines.launch
import me.devnatan.katan.cli.KatanCLI
import me.devnatan.katan.cli.err

class ServerInfoCommand(private val cli: KatanCLI) : CliktCommand(
    name = "info",
    help = "See informations about a server"
) {

    private val serverName by argument("name", "Server name")

    override fun run() {
        val server = try {
            cli.serverManager.getServer(serverName)
        } catch (e: NoSuchElementException) {
            return err("Server $serverName not found.")
        }

        cli.coroutineScope.launch(cli.coroutineExecutor) {
            cli.serverManager.inspectServer(server)
        }.invokeOnCompletion { error ->
            if (error != null)
                return@invokeOnCompletion err(
                    "An error occurred during ${server.name} server inspection.",
                    error.toString()
                )

            echo("Server ID: ${server.id}")
            echo("Name: ${server.name}")
            echo("State: ${server.state}")
        }
    }

}