package org.katan.service.network

import org.katan.model.Connection
import org.katan.model.unit.UnitInstance

public interface NetworkService {

    public suspend fun createConnection(host: String, port: Int): Connection

    public suspend fun attachToNetwork(
        networkName: String,
        instance: UnitInstance
    )

    public suspend fun attachToDefaultNetwork(instance: UnitInstance)
}
