package me.devnatan.katan.core.server

import com.github.dockerjava.api.DockerClient
import me.devnatan.katan.api.server.ServerContainer

class DockerServerContainer(
    id: String,
    private val client: DockerClient
) : ServerContainer(id) {

    override suspend fun start() {
        client.startContainerCmd(id).exec()
    }

    override suspend fun stop() {
        client.stopContainerCmd(id).exec()
    }

}