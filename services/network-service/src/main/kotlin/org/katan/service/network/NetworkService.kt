package org.katan.service.network

import org.katan.model.net.Connection

public interface NetworkService {

    public suspend fun connect(
        networkName: String,
        networkDriver: String?,
        containerId: String,
        host: String?,
        port: Int?
    ): Connection

    public suspend fun createConnection(host: String?, port: Int?): Connection
}
