package org.katan.service.dockerNetwork

import org.apache.logging.log4j.LogManager
import org.katan.config.KatanConfig
import org.katan.model.net.Connection
import org.katan.model.net.InvalidNetworkAssignmentException
import org.katan.model.net.NetworkConnectionFailed
import org.katan.model.net.UnknownNetworkException
import org.katan.service.network.NetworkService
import org.katan.yoki.Yoki
import org.katan.yoki.YokiException
import org.katan.yoki.models.network.Network
import org.katan.yoki.models.network.NetworkHostDriver
import org.katan.yoki.models.network.NetworkMacvlanDriver
import org.katan.yoki.resource.create

internal class DockerNetworkServiceImpl(
    private val config: KatanConfig,
    private val yoki: Yoki
) : NetworkService {

    companion object {
        private const val ALL_INTERFACES = "0.0.0.0"

        private val logger = LogManager.getLogger(DockerNetworkServiceImpl::class.java)
    }

    override suspend fun createConnection(host: String?, port: Int?): Connection {
        return ConnectionImpl(host ?: ALL_INTERFACES, port ?: findRandomPort())
    }

    // TODO find random port
    private suspend fun findRandomPort(): Int {
        return 8080
    }

    override suspend fun connect(
        networkName: String,
        networkDriver: String?,
        containerId: String,
        host: String?,
        port: Int?
    ): Connection {
        var network = getNetwork(networkName).getOrNull()
        if (network == null) {
            if (networkDriver == null) {
                throw UnknownNetworkException(networkName)
            }

            try {
                val createdNetworkId = createNetwork(networkName, networkDriver)
                network = getNetwork(createdNetworkId).getOrThrow()
            } catch (e: YokiException) {
                logger.error(
                    "Failed to connected to $networkName, tried to create missing network but it could not be created.",
                    e
                )
                throw UnknownNetworkException(networkName)
            }
        }

        if (network.isInternal) {
            throw InvalidNetworkAssignmentException(
                "Internal networks cannot be attached: ${network.name}."
            )
        }

        if (network.driver == NetworkHostDriver) {
            logger.warn(
                buildString {
                    append("We recommend that the network of the created instances is not externally ")
                    append("accessible, the network being used (${network.name}) is of the host type, which ")
                    append("exposes the connection of the instances to anyone who wants to access them.")
                }
            )
        }

        if (network.driver == NetworkMacvlanDriver) {
            applyMacvlanIpAddress()
        }

        runCatching {
            yoki.networks.connectContainer(
                id = network.id,
                container = containerId
            )
        }.onFailure { cause ->
            throw NetworkConnectionFailed(network.id, cause)
        }.getOrThrow()

        return createConnection(host, port)
    }

    private suspend fun getNetwork(id: String): Result<Network> {
        return runCatching {
            yoki.networks.inspect(id)
        }
    }

    private suspend fun createNetwork(id: String, driver: String): String {
        logger.info("Creating network $id ($driver)...")

        try {
            return yoki.networks.create {
                this.name = id
                this.driver = driver
            }.id
        } catch (e: YokiException) {
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
