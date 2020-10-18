package me.devnatan.katan.cli.commands.server

import com.github.ajalt.clikt.core.CliktCommand
import com.github.ajalt.clikt.parameters.arguments.argument
import com.github.ajalt.clikt.parameters.options.default
import com.github.ajalt.clikt.parameters.options.option
import com.github.ajalt.clikt.parameters.options.required
import com.github.ajalt.clikt.parameters.options.split
import kotlinx.coroutines.*
import kotlinx.coroutines.channels.consumeEach
import kotlinx.coroutines.channels.sendBlocking
import me.devnatan.katan.api.annotations.InternalKatanApi
import me.devnatan.katan.api.server.*
import me.devnatan.katan.cli.KatanCLI
import me.devnatan.katan.cli.err
import me.devnatan.katan.common.server.ServerCompositionsImpl
import me.devnatan.katan.common.server.UninitializedServer
import me.devnatan.katan.core.server.compositions.image.DockerImageComposition
import me.devnatan.katan.core.server.compositions.image.DockerImageOptions

class ServerCreateCommand(private val cli: KatanCLI) : CliktCommand(
    name = "create",
    help = "Creates a new server"
) {

    private val name by argument(help = "Server name")
    private val image by option("-i", "--image", help = "The Docker image to be used.").required()
    private val compositions by option(
        "-w",
        "--with",
        help = "List of composition definitions to be applied (comma-separated)"
    ).split(",").default(emptyList())

    @OptIn(ExperimentalCoroutinesApi::class, InternalKatanApi::class)
    override fun run() {
        if (cli.serverManager.existsServer(name))
            return err(
                "There is already a server with that name, try another one.",
                "Use \"katan server ls\" to find out which servers already exist."
            )

        runBlocking(CoroutineName("KatanCLI::server-create-main")) {
            var server: Server = UninitializedServer(name, ServerTarget.UNKNOWN)
            echo("Creating server \"$name\"...")
            (server.compositions as ServerCompositionsImpl)[DockerImageComposition] =
                cli.katan.serverManager.compositionFactory.create(
                    DockerImageComposition.Key,
                    DockerImageOptions(image)
                )

            cli.coroutineScope.launch(cli.coroutineExecutor + CoroutineName("KatanCLI::server-create-job")) {
                server = cli.serverManager.createServer(server)
            }.join()

            if (compositions.isNotEmpty()) {
                val length = compositions.size
                echo("Applying $length custom compositions...")

                val applied = arrayListOf<ServerComposition.Key<*>>()
                for ((index, name) in compositions.withIndex()) {
                    val phase = "[${index + 1}/$length]"
                    val factory = cli.serverManager.getCompositionFactory(name)
                    if (factory == null) {
                        err("$phase Factory not found for composition $name.")
                        continue
                    }

                    val key = factory.get(name)
                    if (key == null) {
                        err("$phase $name is registered but not applicable for the specified this factory")
                        continue
                    }

                    /*
                        We need the function __to be executed outside the main thread__ (which runs the CLI),
                        so we launched it on another thread but there are packets received from the adapter
                        that suspend and only return after the result, which is the case of the prompt.

                        So we would need to have the result to continue the execution of the compositions
                        applications, but to have the result we would need to apply the adapters that only
                        suspend after the result which ~~consequently will only return if we apply the compositions~.
                     */
                    val channel = factory.channel.openSubscription()

                    echo("$phase Composing with \"${name}\"...")
                    lateinit var composition: ServerComposition<*>
                    val generation =
                        cli.coroutineScope.async(Dispatchers.IO + CoroutineName("KatanCLI::server-composition-factory")) {
                            /*
                                We will populate the channel first to consume later, as this channel
                                is neither [CONFLATED] nor [RENDEZVOUS], our messages will still be available.

                                We must perform this on a separate thread on account of the [ServerCompositionPacket.Prompt]
                                continuation, if it were on the main thread it would block and it would be impossible to continue.
                             */
                            factory.create(key, ServerCompositionOptions.CLI)
                        }

                    /*
                        It has to be `LAZY` to not be executed immediately and we can execute
                        it after consuming the channel and it also has to be dispatched on another
                        thread because of all the work that we are going to have in the `#write`.
                     */
                    val writer = cli.coroutineScope.launch(
                        Dispatchers.IO + CoroutineName("KatanCLI::server-composition-writer"),
                        CoroutineStart.LAZY
                    ) {
                        // composition.factory = factory
                        composition.write(server)
                        applied.add(key)
                    }

                    // The channel has been consumed
                    generation.invokeOnCompletion {
                        factory.channel.sendBlocking(ServerCompositionPacket.Close)
                        composition = generation.getCompleted()
                        writer.start()
                    }

                    val cancellationHandler = Job()
                    val keyName = factory.get(key)!!

                    /*
                        This `handler` will help us finalize the channel subscription for this composition
                        without throwing an CancellationException and abruptly canceling everything.
                     */
                    launch(cancellationHandler) {
                        channel.consumeEach { packet ->
                            when (packet) {
                                is ServerCompositionPacket.Prompt -> {
                                    val defer = packet.job
                                    prompt("[$keyName] ${packet.text}", packet.defaultValue)?.let {
                                        defer.complete(it)
                                    } ?: defer.completeExceptionally(NullPointerException())
                                }
                                is ServerCompositionPacket.Message -> echo(
                                    "[$keyName] ${packet.text}",
                                    err = packet.error
                                )
                                is ServerCompositionPacket.Close -> {
                                    cancellationHandler.cancel()
                                }
                            }
                        }
                    }

                    cancellationHandler.join()
                }
            }

            cli.serverManager.addServer(server)

            echo("Registering server....")
            cli.serverManager.registerServer(server)

            echo("Inspecting server...")
            cli.serverManager.inspectServer(server)

            echo("Server $name created successfully!")
        }
    }
}