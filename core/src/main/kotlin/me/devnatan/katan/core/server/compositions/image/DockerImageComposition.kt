package me.devnatan.katan.core.server.compositions.image

import me.devnatan.katan.api.server.Server
import me.devnatan.katan.api.server.ServerComposition
import me.devnatan.katan.api.server.ServerCompositionFactory
import me.devnatan.katan.api.server.createCompositionKey

class DockerImageComposition(
    override var options: DockerImageOptions,
    override var factory: ServerCompositionFactory
) : ServerComposition<DockerImageOptions> {

    companion object Key : ServerComposition.Key<DockerImageComposition> by createCompositionKey("image")

    override val key: ServerComposition.Key<*> get() = Key

    override suspend fun read(server: Server) {
        TODO("apply server composition")
    }

    override suspend fun write(server: Server) {
        val image = options.image
        println("Imagem: $image")
    }

}