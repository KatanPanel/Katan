package org.katan.service.network

import org.katan.model.io.HostPort

public interface NetworkService {

    public suspend fun connect(network: String, instance: String, host: String?, port: Short?): HostPort

    public suspend fun createConnection(host: String?, port: Short?): HostPort
}
