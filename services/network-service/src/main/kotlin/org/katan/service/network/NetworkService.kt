package org.katan.service.network

import org.katan.model.Connection

public interface NetworkService {

    public suspend fun createUnitConnection(host: String, port: Int): Connection

}