package org.katan.service.network

import com.github.dockerjava.api.DockerClient
import com.github.dockerjava.api.model.Network
import kotlinx.coroutines.Dispatchers.IO
import kotlinx.coroutines.withContext
import org.apache.logging.log4j.LogManager
import org.katan.model.io.HostPort
import org.katan.model.io.InvalidNetworkAssignmentException
import org.katan.model.io.NetworkConnectionFailed
import org.katan.model.io.UnknownNetworkException

internal class DockerNetworkServiceImpl(
    private val dockerClient: DockerClient
) : NetworkService {

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
        val network = withContext(IO) {
            getNetwork(network)
        }.getOrElse {
            tryCreateNetwork(network)
        }

        if (network.internal) {
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

        runCatching {
            withContext(IO) {
                connectToNetwork(network.id, instance)
            }
        }.recover { cause ->
            throw NetworkConnectionFailed(network.id, cause)
        }.getOrThrow()

        return createConnection(host, port)
    }

    /**
     * Returns an [Result] of [Network] for the given network [id].
     *
     * @param id The network id.
     */
    private fun getNetwork(id: String): Result<Network> {
        return runCatching {
            dockerClient.inspectNetworkCmd().withNetworkId(id).exec()
        }
    }

    /**
     * Connects an [instance] to a [network].
     *
     * @param network The network id.
     * @param instance The instance id (internal container identifier).
     */
    private fun connectToNetwork(network: String, instance: String) {
        dockerClient.connectToNetworkCmd()
            .withContainerId(instance)
            .withNetworkId(network)
            .exec()
    }

    /**
     * Tries to create a network with the given [name] returning the newly created [Network].
     *
     * @param name The network name.
     * @throws UnknownNetworkException If network couldn't be found.
     */
    private suspend fun tryCreateNetwork(name: String): Network {
        val created = createNetwork(name)
        val network = withContext(IO) { getNetwork(name) }

        return network.recover {
            throw UnknownNetworkException(created)
        }.getOrThrow()
    }

    /**
     * Creates a new network with the given [name] and returns its id.
     *
     * @param name The network name.
     */
    private suspend fun createNetwork(name: String): String {
        logger.debug("Creating network {}...", name)

        return withContext(IO) {
            dockerClient.createNetworkCmd().withName(name)
                .exec()
                .id
        }
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
