package me.devnatan.katan.core.impl.server.compositions.image

import com.github.dockerjava.api.model.ExposedPort
import com.github.dockerjava.api.model.HostConfig
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.onStart
import me.devnatan.katan.api.annotations.UnstableKatanApi
import me.devnatan.katan.api.server.Server
import me.devnatan.katan.api.server.ServerComposition
import me.devnatan.katan.api.server.ServerCompositionFactory
import me.devnatan.katan.core.impl.server.DockerServerManager
import me.devnatan.katan.core.impl.server.compositions.DockerCompositionFactory
import org.slf4j.Logger
import org.slf4j.LoggerFactory

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
            e.printStackTrace()
            logger.error("An error occurred while pulling the image \"${image.id}\":", "Cause: ${e.message}")
        }

        logger.info("Creating container (${options.host}:${options.port})...")
        val containerId = katan.docker.createContainerCmd(image.id)
            .withName(server.container.id)
            .withEnv(image.environment.entries.joinToString(" ") { (key, value) ->
                "-e $key=$value"
            })
            .withIpv4Address(options.host)
            .withExposedPorts(ExposedPort.tcp(options.port))
            .withHostConfig(
                HostConfig.newHostConfig()
                    .withNetworkMode("bridge")
                    .withMemory(options.memory)
                    .withMemorySwap(options.memory)
            )
            .exec().id

        logger.debug("Attaching container to \"${DockerServerManager.NETWORK_ID}\" network...")
        katan.docker.connectToNetworkCmd()
            .withContainerId(containerId)
            .withNetworkId(DockerServerManager.NETWORK_ID)
            .exec()
        logger.info("Container " + containerId + " created for ${server.name}.")
    }

}