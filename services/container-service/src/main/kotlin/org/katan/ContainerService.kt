package org.katan

public interface ContainerService {

    public suspend fun getContainer(id: String): Container

    public suspend fun createContainer(options: ContainerCreateOptions): Container

}

internal class ContainerServiceImpl(
    private val containerDiscoveryService: ContainerDiscoveryService,
    private val containerFactory: ContainerFactory
) : ContainerService {

    override suspend fun getContainer(id: String): Container {
        return containerDiscoveryService.find(id)
    }

    override suspend fun createContainer(options: ContainerCreateOptions): Container {
        return containerFactory.create(options)
    }

}