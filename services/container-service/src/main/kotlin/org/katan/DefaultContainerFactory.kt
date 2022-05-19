package org.katan

import org.katan.yoki.Docker
import org.katan.yoki.Yoki
import org.katan.yoki.containers
import org.katan.yoki.resource.container.create

internal class DefaultContainerFactory : ContainerFactory {

    private val dockerClient by lazy { Yoki(Docker) }

    override suspend fun create(options: ContainerCreateOptions): Container {
        return ContainerImpl(
            id = dockerClient.containers.create {
                image = options.image
            }
        )
    }

}