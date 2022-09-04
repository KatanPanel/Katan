package org.katan.service.network

import org.katan.model.net.Connection

interface NetworkService {

    suspend fun connect(
        networkName: String,
        networkDriver: String?,
        containerId: String,
        host: String?,
        port: Int?
    ): Connection

    suspend fun createConnection(host: String?, port: Int?): Connection
}
