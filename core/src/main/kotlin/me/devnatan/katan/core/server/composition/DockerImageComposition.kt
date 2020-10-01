package me.devnatan.katan.core.server.composition

import me.devnatan.katan.api.server.AbstractServerComposition
import me.devnatan.katan.api.server.Server
import me.devnatan.katan.api.server.ServerComposition

class DockerImageComposition : AbstractServerComposition<DockerImageOptions>() {

    companion object Key : ServerComposition.BaseKey<DockerImageComposition>("docker-image")

    override val key: ServerComposition.Key<*> get() = Key

    override suspend fun read(server: Server) {
        TODO("apply server composition")
    }

    override suspend fun write(server: Server) {
        val image = options.image
    }

}