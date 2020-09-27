package me.devnatan.katan.cli.commands

import com.github.ajalt.clikt.core.CliktCommand
import com.github.ajalt.clikt.core.NoOpCliktCommand
import com.github.ajalt.clikt.core.subcommands
import com.github.ajalt.clikt.parameters.arguments.argument
import com.github.ajalt.clikt.parameters.options.associate
import com.github.ajalt.clikt.parameters.options.default
import com.github.ajalt.clikt.parameters.options.option
import com.github.ajalt.clikt.parameters.types.int
import com.github.ajalt.clikt.parameters.types.long
import kotlinx.coroutines.CoroutineName
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.async
import kotlinx.coroutines.launch
import me.devnatan.katan.api.server.isActive
import me.devnatan.katan.api.server.isInactive
import me.devnatan.katan.cli.KatanCLI
import me.devnatan.katan.common.server.UninitializedServer
import java.time.Duration

class ServersCommand(cli: KatanCLI) : NoOpCliktCommand(
    name = "server",
    help = "Create, remove and manage servers",
    printHelpOnEmptyArgs = true
) {

    init {
        subcommands(ServersListCommand(cli),
            ServersCreateCommand(cli),
            ServersStartCommand(cli),
            ServersStopCommand(cli)
        )
    }

}

class ServersListCommand(private val cli: KatanCLI) : CliktCommand(
    name = "ls",
    help = "Lists all servers."
) {

    override fun run() {
        val servers = cli.serverManager.getServerList()
        echo("Registered servers list (${servers.size}):")
        for (server in servers) {
            echo("${server.id}. ${server.name} - ${server.address}:${server.port} [${server.state}] (${server.container.id})")
        }
    }

}

class ServersCreateCommand(private val cli: KatanCLI) : CliktCommand(
    name = "create",
    help = "Creates a new server."
) {

    private val serverName by argument("name", "Server name")
    private val serverConfig by option("-c", "--composition", help = "Server predefined composition").default("default")
    private val serverPort by option("--port", help = "Server exposed port").int().default(1000)
    private val properties by option("-p", "--properties").associate()

    @OptIn(ExperimentalCoroutinesApi::class)
    override fun run() {
        cli.coroutineScope.launch(cli.executor + CoroutineName("KatanCLI::server-create:$serverName")) {
            try {
                val server = UninitializedServer(serverName, "0.0.0.0", serverPort, serverConfig)
                echo("Preparing to create server \"$serverName\"...")
                if (properties.isEmpty())
                    echo("You can set the container initialization variables using -p.")
                else
                    echo(
                        "Defined environment variables: ${
                            properties.mapTo(arrayListOf()) { (k, v) ->
                                "$k: $v"
                            }.joinToString()
                        }"
                    )

                cli.serverManager.createServer(server, properties).also {
                    cli.serverManager.addServer(it)

                    echo("Doing initial container inspection...")
                    cli.serverManager.inspectServer(it)

                    echo("Saving latest content information from the server....")
                    cli.serverManager.registerServer(it)

                    echo("Server ${it.name} created successfully.")
                }
            } catch (e: Throwable) {
                KatanCLI.logger.error(e.message)
            }
        }
    }

}

class ServersStartCommand(private val cli: KatanCLI) : CliktCommand(
    name = "start",
    help = "Starts a server."
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
            cli.coroutineScope.launch(cli.executor) {
                cli.serverManager.startServer(server)
            }
        } catch (e: NoSuchElementException) {
            echo("Server $serverName not found.")
        }
    }

}

class ServersStopCommand(private val cli: KatanCLI) : CliktCommand(
    name = "stop",
    help = "Stops a running server."
) {

    private val serverName by argument("name", "Server name")
    private val timeout by option("-t", "--timeout").long().default(10)

    override fun run() {
        try {
            val server = cli.serverManager.getServer(serverName)
            if (!server.state.isActive()) {
                echo("The server is not running, so it cannot be stopped.")
                return
            }

            echo("Stopping server \"${server.name}\"...")
            cli.coroutineScope.launch(cli.executor) {
                cli.serverManager.stopServer(server, Duration.ofSeconds(timeout))
            }
        } catch (e: NoSuchElementException) {
            echo("Server $serverName not found.")
        }
    }

}