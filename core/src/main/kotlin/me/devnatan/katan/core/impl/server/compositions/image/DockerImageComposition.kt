package me.devnatan.katan.core.impl.server.compositions.image

import com.github.dockerjava.api.model.*
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.collect
import me.devnatan.katan.api.annotations.UnstableKatanApi
import me.devnatan.katan.api.server.Server
import me.devnatan.katan.api.server.ServerComposition
import me.devnatan.katan.api.server.ServerCompositionFactory
import me.devnatan.katan.common.util.replaceBetween
import me.devnatan.katan.core.impl.server.DockerServerManager
import me.devnatan.katan.core.impl.server.compositions.DockerCompositionFactory
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import kotlin.math.pow

@OptIn(UnstableKatanApi::class)
class DockerImageComposition(
    override val factory: ServerCompositionFactory,
    override val options: DockerImageOptions
) : ServerComposition<DockerImageOptions> {

    companion object Key : ServerComposition.Key<DockerImageComposition> {

        private const val ENV_VAR_SEPARATOR = "%"
        const val SERVER_NAME_ENV_KEY = "SERVER_NAME"
        const val SERVER_HOST_ENV_KEY = "SERVER_HOST"
        const val SERVER_PORT_ENV_KEY = "SERVER_PORT"
        const val SERVER_MEMORY_ENV_KEY = "SERVER_MEMORY"

        private val logger: Logger = LoggerFactory.getLogger(DockerImageComposition::class.java)

    }

    override val key: ServerComposition.Key<*> get() = Key

    override suspend fun read(server: Server) {}

    @OptIn(ExperimentalCoroutinesApi::class)
    override suspend fun write(server: Server) {
        val katan = (factory as DockerCompositionFactory).core
        try {
            katan.serverManager.pullImage(options.image).collect {
                logger.debug(it)
            }
        } catch (e: Throwable) {
            logger.error("An error occurred while pulling the image \"${options.image}\":", "Cause: ${e.message}")
            logger.trace(null, e)
            throw e
        }

        logger.debug("Creating container (${options.host}:${options.port})...")

        val memory = (options.memory * 1024.toDouble().pow(2)).toLong()
        val port = ExposedPort.tcp(options.port)
        val containerId = katan.docker.createContainerCmd(options.image)
            .withName(server.container.id)
            .withEnv(options.environment.entries.map { (key, value) ->
                val replacement = value.toString().replaceBetween(ENV_VAR_SEPARATOR) {
                    SERVER_NAME_ENV_KEY by server.name
                    SERVER_HOST_ENV_KEY by server.host
                    SERVER_PORT_ENV_KEY by server.port
                    SERVER_MEMORY_ENV_KEY by options.memory
                }

                "$key=$replacement"
            }.toList())
            .withExposedPorts(port)
            .withHostName(server.container.id)
            .withTty(true)
            .withAttachStdin(true)
            .withAttachStderr(true)
            .withAttachStdout(true)
            .withStdinOpen(true)
            .withHostConfig(
                HostConfig.newHostConfig()
                    .withPortBindings(Ports(PortBinding(Ports.Binding.bindIpAndPort(options.host, options.port), port)))
                    .withMemory(memory)
                    .withMemorySwap(memory)
            ).withVolumes(Volume("/data")).exec().id

        logger.debug("Attaching container to \"${DockerServerManager.NETWORK_ID}\" network...")
        katan.docker.connectToNetworkCmd()
            .withContainerId(containerId)
            .withNetworkId(DockerServerManager.NETWORK_ID)
            .exec()
        logger.debug("Container " + containerId + " created for ${server.name}.")
    }

}