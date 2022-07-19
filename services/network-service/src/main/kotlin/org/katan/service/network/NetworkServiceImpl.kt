package org.katan.service.network

import org.katan.model.Connection

internal class NetworkServiceImpl : NetworkService {

    override suspend fun createUnitConnection(host: String, port: Int): Connection {
        return ConnectionImpl(host, port)
    }

}