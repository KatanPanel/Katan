package me.devnatan.katan.core.impl.server

import com.github.dockerjava.api.DockerClient
import me.devnatan.katan.api.server.ServerContainer

class DockerServerContainer(id: String, name: String, private val client: DockerClient) : ServerContainer(id, name) {

    override suspend fun start() {
        client.startContainerCmd(id).exec()
    }

    override suspend fun stop() {
        client.stopContainerCmd(id).exec()
    }

    fun stop(timeout: Int) {
        client.stopContainerCmd(id).withTimeout(timeout).exec()
    }

}