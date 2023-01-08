package org.katan.service.network

import org.katan.model.io.HostPort

interface NetworkService {

    suspend fun connect(network: String, instance: String, host: String?, port: Short?): HostPort

    suspend fun createConnection(host: String?, port: Short?): HostPort
}
