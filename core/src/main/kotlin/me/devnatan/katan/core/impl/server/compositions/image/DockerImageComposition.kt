package me.devnatan.katan.core.impl.server.compositions.image

import com.github.dockerjava.api.model.*
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.onStart
import me.devnatan.katan.api.annotations.UnstableKatanApi
import me.devnatan.katan.api.server.Server
import me.devnatan.katan.api.server.ServerComposition
import me.devnatan.katan.api.server.ServerCompositionFactory
import me.devnatan.katan.common.util.replaceVars
import me.devnatan.katan.core.impl.server.DockerServerManager
import me.devnatan.katan.core.impl.server.ServerImpl
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

        private val logger: Logger = LoggerFactory.getLogger(DockerImageComposition::class.java)

    }

    override val key: ServerComposition.Key<*> get() = Key

    override suspend fun read(server: Server) {}

    @OptIn(ExperimentalCoroutinesApi::class)
    override suspend fun write(server: Server) {
        val image = options.image
        val katan = (factory as DockerCompositionFactory).core
        try {
            katan.serverManager.pullImage(image.id).onStart {
                logger.info("Pulling image \"${image.id}\"...")
            }.collect { logger.info(it) }
        } catch (e: Throwable) {
            logger.error("An error occurred while pulling the image \"${image.id}\":", "Cause: ${e.message}")
            e.printStackTrace()
        }

        logger.info("Creating container (${options.host}:${options.port})...")

        val memory = (options.memory * 1024.toDouble().pow(2)).toLong()
        val port = ExposedPort.tcp(options.port)
        val containerId = katan.docker.createContainerCmd(image.id)
            .withName(server.container.id)
            .withEnv(image.environment.entries.map { (key, value) ->
                "$key=${
                    value.toString().replaceVars {
                        ServerImpl.HOST_ENV by server.host
                        ServerImpl.PORT_ENV by server.port
                    }
                }"
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
        logger.info("Container " + containerId + " created for ${server.name}.")
    }

}