package me.devnatan.katan.core.impl.server.compositions

import me.devnatan.katan.api.server.composition.*
import me.devnatan.katan.core.KatanCore

class DockerImageCompositionFactory(val core: KatanCore) : CompositionFactory() {

    init {
        registerNamedKey("docker-image", DockerImageComposition)
    }

    override suspend fun create(key: Composition.Key): Composition<*> {
        check(key is DockerImageComposition) { "Unsupported key: $key" }
        return DockerImageCompositionImpl
    }

    override suspend fun generate(key: Composition.Key, data: Map<String, Any>): CompositionOptions {
        val host: String by data
        val port: Number by data
        val memory: Number by data
        val image: String by data
        val environment: Map<String, Any> by data

        return DockerImageComposition.Options(host, port.toInt(), memory.toLong(), image, environment)
    }

}