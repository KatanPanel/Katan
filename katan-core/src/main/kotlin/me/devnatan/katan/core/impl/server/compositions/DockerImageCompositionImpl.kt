package me.devnatan.katan.core.impl.server.compositions

import com.github.dockerjava.api.model.*
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.flow.collect
import me.devnatan.katan.api.composition.CompositionFactory
import me.devnatan.katan.api.composition.CompositionStore
import me.devnatan.katan.api.composition.DockerImageComposition
import me.devnatan.katan.api.logging.logger
import me.devnatan.katan.api.server.Server
import me.devnatan.katan.common.util.replaceBetween
import me.devnatan.katan.core.KatanCore
import me.devnatan.katan.core.impl.server.ServerImpl
import me.devnatan.katan.core.impl.server.docker.DockerServerContainer
import me.devnatan.katan.core.impl.server.docker.DockerServerManager
import me.devnatan.katan.core.util.attachResultCallback
import org.slf4j.Logger
import kotlin.math.pow

object DockerImageCompositionImpl : DockerImageComposition {

    private val log: Logger = logger<DockerImageComposition>()

    override suspend fun write(
        server: Server,
        store: CompositionStore<DockerImageComposition.Options>,
        factory: CompositionFactory
    ) {
        if (server !is ServerImpl)
            throw IllegalArgumentException("Cannot use Docker Image Server Composition directly.")

        val (options) = store
        val katan = (factory as DockerImageCompositionFactory).core

        try {
            pullImage(options.image, katan).collect {
                log.debug("[Pull]: $it")
            }
        } catch (e: Throwable) {
            log.warn("Failed to push ${server.name} image (${options.image}):")
            log.warn(e.toString())
        }

        log.debug("Creating container (${options.host}:${options.port})...")
        options.environment = options.environment.mapValues { (_, value) ->
            value.toString().replaceBetween("%") {
                "SERVER_NAME" by server.name
                "SERVER_HOST" by server.host
                "SERVER_PORT" by server.port
                "SERVER_MEMORY" by options.memory
            }
        }

        val memory = (options.memory * 1024.toDouble().pow(2)).toLong()
        val port = ExposedPort.tcp(options.port)
        val containerId = katan.docker.client.createContainerCmd(options.image)
            .withName(server.container.name)
            .withEnv(options.environment.map { (key, value) ->
                "$key=$value"
            }.toList())
            .withExposedPorts(port)
            .withHostName(server.container.name)
            .withTty(true)
            .withAttachStdin(true)
            .withAttachStderr(true)
            .withAttachStdout(true)
            .withStdinOpen(true)
            .withLabels(
                mapOf(
                    "katan.server.id" to server.id.toString(),
                    "katan.server.name" to server.name
                )
            )
            .withHostConfig(
                HostConfig.newHostConfig()
                    .withPortBindings(
                        Ports(
                            PortBinding(
                                Ports.Binding.bindIpAndPort(
                                    options.host,
                                    options.port
                                ), port
                            )
                        )
                    )
                    .withMemory(memory)
                    .withMemorySwap(memory)
            ).withVolumes(Volume("/data")).exec().id

        server.container = DockerServerContainer(
            containerId,
            server.container.name,
            katan.docker.client
        )
        log.debug("Attaching ${server.container.name} to \"${DockerServerManager.NETWORK_ID}\" network...")
        katan.docker.client.connectToNetworkCmd()
            .withContainerId(containerId)
            .withNetworkId(DockerServerManager.NETWORK_ID)
            .exec()
        log.debug("Container " + containerId + " created for ${server.name}.")
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    private fun pullImage(image: String, katan: KatanCore): Flow<String> =
        callbackFlow {
            katan.docker.client.pullImageCmd(image)
                .exec(attachResultCallback<PullResponseItem, String> {
                    it.toString()
                })
            awaitClose()
        }

}