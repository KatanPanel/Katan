package me.devnatan.katan.cli.commands.server

import com.github.ajalt.clikt.core.CliktCommand
import com.github.ajalt.clikt.parameters.arguments.argument
import com.github.ajalt.clikt.parameters.options.default
import com.github.ajalt.clikt.parameters.options.option
import com.github.ajalt.clikt.parameters.types.long
import kotlinx.coroutines.launch
import me.devnatan.katan.api.server.isInactive
import me.devnatan.katan.cli.KatanCLI
import java.time.Duration

class ServerStopCommand(private val cli: KatanCLI) : CliktCommand(
    name = "stop",
    help = "Stops a running server"
) {

    private val serverName by argument("name", "Server name")
    private val timeout by option("-t", "--timeout").long().default(10)

    override fun run() {
        try {
            val server = cli.serverManager.getServer(serverName)
            if (server.state.isInactive()) {
                echo("This server is not running, so it cannot be stopped.")
                return
            }

            echo("Stopping server \"${server.name}\"...")
            cli.coroutineScope.launch(cli.coroutineExecutor) {
                cli.serverManager.stopServer(server, Duration.ofSeconds(timeout))
            }.invokeOnCompletion { error ->
                if (error != null) {
                    echo("An error occurred during ${server.name} server stop.")
                    echo(error)
                    return@invokeOnCompletion
                }


            }
        } catch (e: NoSuchElementException) {
            echo("Server $serverName not found.")
        }
    }

}