package me.devnatan.katan.core.impl.server.compositions

import me.devnatan.katan.api.annotations.InternalKatanApi
import me.devnatan.katan.api.server.*
import me.devnatan.katan.core.KatanCore

class DockerImageServerCompositionFactory(val core: KatanCore) : ServerCompositionFactory() {

    init {
        addSupportedKey("docker-image", DockerImageServerComposition)
    }

    @InternalKatanApi
    override suspend fun create(
        key: ServerComposition.Key<*>,
        options: ServerCompositionOptions
    ): ServerComposition<*> {
        return DockerImageServerCompositionImpl(this, options as DockerImageServerComposition.Options)
    }

    override suspend fun generate(key: ServerComposition.Key<*>, data: Map<String, Any>): ServerCompositionOptions {
        val host: String by data
        val port: Number by data
        val memory: Number by data
        val image: String by data
        val environment: Map<String, Any> by data

        return DockerImageServerComposition.Options(host, port.toInt(), memory.toLong(), image, environment)
    }

}