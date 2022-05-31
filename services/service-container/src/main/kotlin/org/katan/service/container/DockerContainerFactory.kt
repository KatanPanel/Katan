package org.katan.service.container

import org.katan.yoki.Docker
import org.katan.yoki.Yoki
import org.katan.yoki.containers
import org.katan.yoki.resource.container.create
import java.util.UUID

internal class DockerContainerFactory : ContainerFactory {

    private val dockerClient by lazy {
        Yoki(Docker)
    }

    override suspend fun generateId(): String {
        return UUID.randomUUID().toString()
    }

    override suspend fun create(options: ContainerCreateOptions): Container {
        return ContainerImpl(
            id = generateId(),
            runtimeIdentifier = dockerClient.containers.create {
                image = options.image
            }
        )
    }

}