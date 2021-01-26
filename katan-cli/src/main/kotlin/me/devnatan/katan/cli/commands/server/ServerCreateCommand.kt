package me.devnatan.katan.cli.commands.server

import com.github.ajalt.clikt.core.CliktCommand
import com.github.ajalt.clikt.parameters.arguments.argument
import com.github.ajalt.clikt.parameters.options.default
import com.github.ajalt.clikt.parameters.options.option
import com.github.ajalt.clikt.parameters.options.required
import com.github.ajalt.clikt.parameters.options.split
import com.github.ajalt.clikt.parameters.types.int
import com.github.ajalt.clikt.parameters.types.long
import kotlinx.coroutines.*
import kotlinx.coroutines.channels.consumeEach
import kotlinx.coroutines.channels.sendBlocking
import me.devnatan.katan.api.annotations.InternalKatanApi
import me.devnatan.katan.api.annotations.UnstableKatanApi
import me.devnatan.katan.api.game.GameVersion
import me.devnatan.katan.api.server.*
import me.devnatan.katan.cli.KatanCLI
import me.devnatan.katan.cli.err
import me.devnatan.katan.common.KatanTranslationKeys.CLI_ALIAS_SERVER_CREATE
import me.devnatan.katan.common.KatanTranslationKeys.CLI_ARG_HELP_SERVER_NAME
import me.devnatan.katan.common.KatanTranslationKeys.CLI_ARG_LABEL_SERVER_NAME
import me.devnatan.katan.common.KatanTranslationKeys.CLI_HELP_SERVER_CREATE
import me.devnatan.katan.common.KatanTranslationKeys.CLI_SERVER_NOT_FOUND
import me.devnatan.katan.common.impl.server.ServerGameImpl

class ServerCreateCommand(private val cli: KatanCLI) : CliktCommand(
    name = cli.translate(CLI_ALIAS_SERVER_CREATE),
    help = cli.translate(CLI_HELP_SERVER_CREATE)
) {

    private val name by argument(
        cli.translate(CLI_ARG_LABEL_SERVER_NAME),
        cli.translate(CLI_ARG_HELP_SERVER_NAME)
    )

    private val game by option("-g", "--game", help = "A game that is supported by Katan.").required()
    private val host by option(
        "-h",
        "--host",
        help = "Remote server connection address."
    ).default("0.0.0.0")

    private val port by option("-p", "--port", help = "Remote server connection port.").int().required()
    private val image by option("-i", "--image", help = "Docker image that will be used to build the server.")
    private val compositions by option(
        "-w",
        "--with",
        help = "List of compositions to be applied to the server (comma-separated)."
    ).split(",").default(emptyList())
    private val memory by option("-m", "--memory", help = "Amount of memory to allocate on the server (in MB).").long()
        .default(1024)

    @OptIn(ExperimentalCoroutinesApi::class, InternalKatanApi::class, UnstableKatanApi::class)
    override fun run() {
        if (cli.serverManager.existsServer(name))
            return err(cli.translate(CLI_SERVER_NOT_FOUND, name))

        val gameTarget = game.split(":")
        val target = cli.katan.gameManager.getGame(gameTarget[0])
            ?: return err("Game \"$game\" is invalid or unsupported.")

        val version: GameVersion? = if (gameTarget.size > 1) {
            val gameVersion = gameTarget[1].replace("-", " ")
            target.versions.find { it.name.equals(gameVersion, true) }
                ?: return err("Game version \"${gameVersion}\" not found for ${target.name} (available: ${target.versions.joinToString { it.name }}).")
        } else null

        if (port !in target.settings.ports)
            return err("The port $port does not respect the ${target.name}'s default port range (${target.settings.ports}).")

        val targetImage = (image ?: (version?.image ?: target.image)) ?: return err("No image was provided.")

        val completion = Job()
        runBlocking(CoroutineName("KatanCLI::server-create-main")) {
            val server =
                cli.katan.serverManager.prepareServer(name, ServerGameImpl(target.type, version), host, port.toShort())

            completion.invokeOnCompletion { error ->
                if (error == null) {
                    cli.coroutineScope.launch(CoroutineName("KatanCLI::server-create")) {
                        cli.serverManager.addServer(server)
                        cli.serverManager.registerServer(server)
                        cli.serverManager.inspectServer(server)
                    }
                }
            }

            server.compositions[DockerImageServerComposition] = cli.katan.serverManager.getCompositionFactory(DockerImageServerComposition)!!.create(
                DockerImageServerComposition,
                DockerImageServerComposition.Options(
                    host,
                    port,
                    memory,
                    targetImage,
                    version?.environment?.let {
                        target.environment + it
                    } ?: target.environment
                )
            )

            cli.coroutineScope.launch(completion + CoroutineName("KatanCLI::server-create-job")) {
                cli.serverManager.createServer(server)
            }.join()

            if (compositions.isNotEmpty()) {
                val length = compositions.size
                echo("Applying $length compositions...")

                val applied = arrayListOf<ServerComposition.Key<*>>()
                for ((index, name) in compositions.withIndex()) {
                    val phase = "[${index + 1}/$length]"
                    val factory = cli.serverManager.getCompositionFactory(name)
                    if (factory == null) {
                        err("$phase Factory not found for composition $name.")
                        continue
                    }

                    val key = factory[name]
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
                    val keyName = factory[key]!!

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

            completion.complete()
        }
    }
}