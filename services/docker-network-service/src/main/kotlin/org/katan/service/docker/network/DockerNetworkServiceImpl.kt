package org.katan.service.docker.network

import org.katan.config.KatanConfig
import org.katan.model.net.Connection
import org.katan.model.unit.UnitInstance
import org.katan.service.network.NetworkService

internal class DockerNetworkServiceImpl(
    private val config: KatanConfig
) : NetworkService {

    companion object {
        private const val NETWORK_MACVLAN = "macvlan"
    }

    override suspend fun createConnection(host: String, port: Int): Connection {
        return ConnectionImpl(host, port)
    }

    override suspend fun attachToNetwork(networkName: String, instance: UnitInstance) {
        checkForNetworkAvailability(networkName)
        if (networkName == NETWORK_MACVLAN) {
            applyMacvlanIpAddress()
        }
    }

    override suspend fun attachToDefaultNetwork(instance: UnitInstance) {
        attachToNetwork(config.docker.network.name, instance)
    }

    /**
     * Checks if a network is available for a container to be attached to, internal networks cannot
     * be attached.
     */
    private fun checkForNetworkAvailability(network: String) {
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

        throw IllegalStateException("Macvlan as Docker network driver is not supported")
    }
}
