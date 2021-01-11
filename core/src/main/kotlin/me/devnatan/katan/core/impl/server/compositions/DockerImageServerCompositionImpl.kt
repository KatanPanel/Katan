package me.devnatan.katan.core.impl.server.compositions

import com.github.dockerjava.api.model.*
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.InternalCoroutinesApi
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.flow.collect
import me.devnatan.katan.api.server.DockerImageServerComposition
import me.devnatan.katan.api.server.Server
import me.devnatan.katan.api.server.ServerComposition
import me.devnatan.katan.api.server.ServerCompositionFactory
import me.devnatan.katan.common.util.replaceBetween
import me.devnatan.katan.core.KatanCore
import me.devnatan.katan.core.impl.server.DockerServerContainer
import me.devnatan.katan.core.impl.server.DockerServerManager
import me.devnatan.katan.core.impl.server.ServerImpl
import me.devnatan.katan.core.util.attachResultCallback
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import kotlin.math.pow

class DockerImageServerCompositionImpl(
    override val factory: ServerCompositionFactory,
    override val options: DockerImageServerComposition.Options
) : DockerImageServerComposition {

    companion object {

        private val logger: Logger = LoggerFactory.getLogger(DockerImageServerComposition::class.java)

    }

    override val key: ServerComposition.Key<*> get() = DockerImageServerComposition

    override suspend fun read(server: Server) {}

    @OptIn(ExperimentalCoroutinesApi::class, InternalCoroutinesApi::class)
    override suspend fun write(server: Server) {
        if (server !is ServerImpl)
            throw IllegalArgumentException("Cannot use Docker Image Server Composition directly.")

        val katan = (factory as DockerImageServerCompositionFactory).core

        try {
            pullImage(options.image, katan).collect {
                logger.debug("[Pull]: $it")
            }
        } catch (e: Throwable) {
            logger.warn("Failed to push ${server.name} image (${options.image}):")
            logger.warn(e.toString())
        }

        logger.debug("Creating container (${options.host}:${options.port})...")
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
            .withLabels(mapOf(
                "katan.server.id" to server.id.toString(),
                "katan.server.name" to server.name
            ))
            .withHostConfig(
                HostConfig.newHostConfig()
                    .withPortBindings(Ports(PortBinding(Ports.Binding.bindIpAndPort(options.host, options.port), port)))
                    .withMemory(memory)
                    .withMemorySwap(memory)
            ).withVolumes(Volume("/data")).exec().id

        server.container = DockerServerContainer(containerId, server.container.name, katan.docker.client)
        logger.debug("Attaching ${server.container.name} to \"${DockerServerManager.NETWORK_ID}\" network...")
        katan.docker.client.connectToNetworkCmd()
            .withContainerId(containerId)
            .withNetworkId(DockerServerManager.NETWORK_ID)
            .exec()
        logger.debug("Container " + containerId + " created for ${server.name}.")
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    private fun pullImage(image: String, katan: KatanCore): Flow<String> = callbackFlow {
        katan.docker.client.pullImageCmd(image).exec(attachResultCallback<PullResponseItem, String> {
            it.toString()
        })
        awaitClose()
    }

}