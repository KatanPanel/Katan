package org.katan

import kotlinx.coroutines.cancel
import org.katan.yoki.Docker
import org.katan.yoki.Yoki
import org.katan.yoki.containers
import org.katan.yoki.resource.container.create

internal class DefaultContainerFactory(
    dockerClient: Lazy<Yoki> = lazy { Yoki(Docker) }
) : ContainerFactory {

    private val dockerClient by dockerClient

    override suspend fun create(options: ContainerCreateOptions): Container {
        return ContainerImpl(
            id = dockerClient.containers.create {
                image = options.image
            }
        )
    }

    override suspend fun close() {
        dockerClient.cancel()
    }

}