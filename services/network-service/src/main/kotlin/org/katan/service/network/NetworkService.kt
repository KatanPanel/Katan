package org.katan.service.network

import org.katan.model.instance.UnitInstance
import org.katan.model.net.Connection

public interface NetworkService {

    public suspend fun createConnection(host: String, port: Int): Connection

    public suspend fun attachToNetwork(
        networkName: String,
        instance: UnitInstance
    )

    public suspend fun attachToDefaultNetwork(instance: UnitInstance)
}
