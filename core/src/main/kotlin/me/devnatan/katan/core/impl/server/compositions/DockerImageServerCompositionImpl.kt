package me.devnatan.katan.core.impl.server.compositions

import com.github.dockerjava.api.model.*
import kotlinx.coroutines.ExperimentalCoroutinesApi
import me.devnatan.katan.api.annotations.UnstableKatanApi
import me.devnatan.katan.api.server.DockerImageServerComposition
import me.devnatan.katan.api.server.Server
import me.devnatan.katan.api.server.ServerComposition
import me.devnatan.katan.api.server.ServerCompositionFactory
import me.devnatan.katan.common.util.replaceBetween
import me.devnatan.katan.core.impl.server.DockerServerManager
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import kotlin.math.pow

@OptIn(UnstableKatanApi::class)
class DockerImageServerCompositionImpl(
    override val factory: ServerCompositionFactory,
    override val options: DockerImageServerComposition.Options
) : DockerImageServerComposition {

    companion object {

        private val logger: Logger = LoggerFactory.getLogger(DockerImageServerComposition::class.java)

    }

    override val key: ServerComposition.Key<*> get() = DockerImageServerComposition

    override suspend fun read(server: Server) {}

    @OptIn(ExperimentalCoroutinesApi::class)
    override suspend fun write(server: Server) {
        val katan = (factory as DockerImageServerCompositionFactory).core
        /* katan.serverManager.pullImage(options.image).collect {
            logger.debug(it)
        } */

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
        val containerId = katan.docker.createContainerCmd(options.image)
            .withName(server.container.id)
            .withEnv(options.environment.map { (key, value) ->
                "$key=$value"
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