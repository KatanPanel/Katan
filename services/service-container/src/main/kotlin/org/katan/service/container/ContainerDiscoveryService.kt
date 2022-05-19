package org.katan.service.container

public interface ContainerDiscoveryService {

    public suspend fun find(id: String): Container

}

internal class ContainerDiscoveryServiceImpl : ContainerDiscoveryService {

    override suspend fun find(id: String): Container {
        TODO("Not yet implemented")
    }

}