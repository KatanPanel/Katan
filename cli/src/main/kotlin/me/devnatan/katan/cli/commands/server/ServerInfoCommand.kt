package me.devnatan.katan.cli.commands.server

import com.github.ajalt.clikt.core.CliktCommand
import com.github.ajalt.clikt.parameters.arguments.argument
import com.github.ajalt.clikt.parameters.options.flag
import com.github.ajalt.clikt.parameters.options.option
import kotlinx.coroutines.launch
import me.devnatan.katan.cli.KatanCLI

class ServerInfoCommand(private val cli: KatanCLI) : CliktCommand(
    name = "info",
    help = "See informations about a server"
) {

    private val serverName by argument("name", "Server name")
    private val noUpdate by option("--no-update").flag()

    override fun run() {
        try {
            val server = cli.serverManager.getServer(serverName)
            val showInfo: (Throwable?) -> Unit = { error ->
                if (error != null) {
                    echo("An error occurred during ${server.name} server inspection.")
                    echo(error)
                } else {
                    echo("----------------- Server Info -----------------")
                    echo("ID: ${server.id}")
                    echo("Name: ${server.name}")
                    echo("Composition: ${server.compositions}")
                }
            }

            if (!noUpdate) {
                if (!server.container.isInspected()) {
                    echo("The server has never been inspected and the option to not update is enabled, remove the option for an initial inspection to be performed")
                    return
                }

                echo("Inspecting server...")
                cli.coroutineScope.launch(cli.coroutineExecutor) {
                    cli.serverManager.inspectServer(server)
                }.invokeOnCompletion { error ->
                    showInfo(error)
                }
            }

            showInfo(null)
        } catch (e: NoSuchElementException) {
            echo("Server $serverName not found.")
        }
    }

}