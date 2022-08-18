package org.katan.service.docker.network

import com.github.dockerjava.api.DockerClient
import com.github.dockerjava.api.exception.DockerException
import com.github.dockerjava.api.model.Network
import kotlinx.coroutines.Dispatchers.IO
import kotlinx.coroutines.withContext
import org.apache.logging.log4j.LogManager
import org.katan.config.KatanConfig
import org.katan.model.net.Connection
import org.katan.model.net.InvalidNetworkAssignmentException
import org.katan.model.net.NetworkConnectionFailed
import org.katan.model.net.UnknownNetworkException
import org.katan.service.network.NetworkService

internal class DockerNetworkServiceImpl(
    private val config: KatanConfig,
    private val dockerClient: DockerClient
) : NetworkService {

    companion object {
        private const val ALL_INTERFACES = "0.0.0.0"

        private const val MACVLAN_DRIVER = "macvlan"
        private const val HOST_DRIVER = "host"

        private val logger = LogManager.getLogger(DockerNetworkServiceImpl::class.java)
    }

    override suspend fun createConnection(host: String?, port: Int?): Connection {
        return ConnectionImpl(host ?: ALL_INTERFACES, port ?: findRandomPort())
    }

    private suspend fun findRandomPort(): Int {
        // TODO random port
        return 8080
    }

    override suspend fun connect(
        networkName: String,
        networkDriver: String?,
        containerId: String,
        host: String?,
        port: Int?
    ): Connection {
        var network = withContext(IO) { getNetworkOrNull(networkName) }

        if (network == null) {
            if (networkDriver == null) {
                throw UnknownNetworkException(networkName)
            }

            try {
                val createdNetworkId = createNetwork(networkName, networkDriver)
                network = withContext(IO) { getNetworkOrNull(createdNetworkId)!! }
            } catch (e: DockerException) {
                logger.error(
                    "Failed to connected to $networkName, tried to create missing network but it could not be created.",
                    e
                )
                throw UnknownNetworkException(networkName)
            }
        }

        if (network.internal) {
            throw InvalidNetworkAssignmentException(
                "Internal networks cannot be attached: ${network.name}."
            )
        }

        if (network.driver.equals(HOST_DRIVER)) {
            logger.warn(
                buildString {
                    append("We recommend that the network of the created instances is not externally ")
                    append("accessible, the network being used (${network.name}) is of the host type, which ")
                    append("exposes the connection of the instances to anyone who wants to access them.")
                }
            )
        }

        if (network.driver == MACVLAN_DRIVER) {
            applyMacvlanIpAddress()
        }

        runCatching {
            withContext(IO) {
                connectToNetwork(network.id, containerId)
            }
        }.onFailure { cause ->
            throw NetworkConnectionFailed(network.id, cause)
        }.getOrThrow()

        return createConnection(host, port)
    }

    private suspend fun getNetworkOrNull(id: String): Network? {
        return runCatching {
            dockerClient.inspectNetworkCmd().withNetworkId(id).exec()
        }.getOrNull()
    }

    private fun connectToNetwork(networkId: String, containerId: String) {
        dockerClient.connectToNetworkCmd()
            .withContainerId(containerId)
            .withNetworkId(networkId)
            .exec()
    }

    private suspend fun createNetwork(id: String, driver: String): String {
        logger.info("Creating network $id ($driver)...")

        try {
            return withContext(IO) {
                dockerClient.createNetworkCmd().withName(id).withDriver(driver)
                    .exec()
                    .id
            }
        } catch (e: DockerException) {
            logger.error("Failed to create network: $id ($driver)", e)
            throw e
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

        throw InvalidNetworkAssignmentException(
            "Macvlan as Docker network driver is not supported for now"
        )
    }
}
