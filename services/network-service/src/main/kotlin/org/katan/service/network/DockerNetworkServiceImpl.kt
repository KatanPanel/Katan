package org.katan.service.network

import kotlinx.coroutines.Dispatchers.IO
import kotlinx.coroutines.withContext
import me.devnatan.yoki.Yoki
import me.devnatan.yoki.YokiException
import me.devnatan.yoki.models.network.Network
import me.devnatan.yoki.resource.NetworkNotFoundException
import me.devnatan.yoki.resource.network.create
import org.apache.logging.log4j.LogManager
import org.katan.model.net.HostPort
import org.katan.model.net.InvalidNetworkAssignmentException
import org.katan.model.net.NetworkConnectionFailed
import org.katan.model.net.UnknownNetworkException

internal class DockerNetworkServiceImpl(private val dockerClient: Yoki) : NetworkService {

    companion object {
        private const val ALL_INTERFACES = "0.0.0.0"
        private const val MACVLAN_DRIVER = "macvlan"
        internal const val HOST_DRIVER = "host"

        private val logger = LogManager.getLogger(DockerNetworkServiceImpl::class.java)
    }

    override suspend fun createConnection(host: String?, port: Short?): HostPort {
        return HostPort(host ?: ALL_INTERFACES, port ?: findRandomPort())
    }

    override suspend fun connect(
        network: String,
        instance: String,
        host: String?,
        port: Short?
    ): HostPort {
        val network = runCatching {
            withContext(IO) { dockerClient.networks.inspect(network) }
        }.recoverCatching {
            tryCreateNetwork(network)
        }.getOrThrow()

        if (network.isInternal) {
            throw InvalidNetworkAssignmentException("Internal networks cannot be connected to")
        }

        when (network.driver) {
            HOST_DRIVER -> logger.warn(
                "We recommend that the network of the created instances is not externally " +
                    "accessible, the network being used ({}) is of the \"host\" type, which " +
                    "exposes the connection of the instances to anyone who wants to access them.",
                network.name
            )

            MACVLAN_DRIVER -> applyMacvlanIpAddress()
        }

        try {
            withContext(IO) {
                dockerClient.networks.connectContainer(
                    id = network.id,
                    container = instance
                )
            }
        } catch (exception: YokiException) {
            throw NetworkConnectionFailed(network.id, exception)
        }

        return createConnection(host, port)
    }

    /**
     * Tries to create a network with the given [name] returning the newly created [Network].
     *
     * @param name The network name.
     * @throws UnknownNetworkException If network couldn't be found.
     */
    private suspend fun tryCreateNetwork(name: String): Network {
        val created = createNetwork(name)
        return try {
            dockerClient.networks.inspect(name)
        } catch (e: NetworkNotFoundException) {
            throw UnknownNetworkException(created)
        }
    }

    /**
     * Creates a new network with the given [name] and returns its id.
     *
     * @param name The network name.
     */
    private suspend fun createNetwork(name: String): String {
        logger.debug("Creating network {}...", name)
        return dockerClient.networks.create { this.name = name }.id
    }

    /**
     * By default, Docker always use the 1st interface for outbound traffic and can lead to issues
     * like being unable to use two ip addresses on the same none.
     *
     * Macvlan network driver allows IP addresses to be manually assigned to the container so that
     * the container does not use random IPs.
     */
    @Suppress("RedundantSuspendModifier")
    private suspend fun applyMacvlanIpAddress() {
        // TODO check ip address conflicts and apply proper NetworkingConfig
        // https://docs.docker.com/engine/api/v1.41/#tag/Container/operation/ContainerCreate

        throw InvalidNetworkAssignmentException("Macvlan network is not supported")
    }

    @Suppress("RedundantSuspendModifier")
    private suspend fun findRandomPort(): Short {
        TODO("Unable to find a random port")
    }
}
