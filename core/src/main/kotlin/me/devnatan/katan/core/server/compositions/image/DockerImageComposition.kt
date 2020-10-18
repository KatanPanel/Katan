package me.devnatan.katan.core.server.compositions.image

import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.onStart
import me.devnatan.katan.api.annotations.UnstableKatanApi
import me.devnatan.katan.api.server.Server
import me.devnatan.katan.api.server.ServerComposition
import me.devnatan.katan.api.server.ServerCompositionFactory
import me.devnatan.katan.core.manager.DockerServerManager
import me.devnatan.katan.core.server.compositions.DockerCompositionFactory
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

    override suspend fun read(server: Server) {

    }

    @OptIn(ExperimentalCoroutinesApi::class)
    override suspend fun write(server: Server) {
        val image = options.image
        val katan = (factory as DockerCompositionFactory).core
        try {
            katan.serverManager.pullImage(image).onStart {
                logger.info("Pulling image \"$image\"...")
            }.collect { logger.info(it) }

            logger.info("Creating container...")
            val containerId = katan.docker.createContainerCmd(image)
                .withName(server.container.id)
                .exec().id

            logger.info("Attaching container to \"${DockerServerManager.NETWORK_ID}\" network...")
            katan.docker.connectToNetworkCmd()
                .withContainerId(containerId)
                .withNetworkId(DockerServerManager.NETWORK_ID)
                .exec()
            logger.info("Container " + containerId + " created for ${server.name}.")
        } catch (e: Throwable) {
            e.printStackTrace()
            logger.error("An error occurred while pulling the image \"$image\":", "Cause: ${e.message}")
        }
    }

}