package org.katan.service.server

import org.katan.service.container.ContainerCreateOptions
import org.katan.service.container.ContainerFactory
import java.util.UUID

internal class DefaultServerFactory(
    private val containerFactory: ContainerFactory
) : ServerFactory {

    override suspend fun create(options: ServerCreateOptions): Server {
        return ServerImpl(
            id = UUID.randomUUID().toString(),
            name = options.name,
            container = containerFactory.create(ContainerCreateOptions("itzg/minecraft-server"))
        )
    }

}