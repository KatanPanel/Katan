package me.devnatan.katan.core.impl.server.compositions.image

import me.devnatan.katan.api.server.ServerCompositionOptions

class DockerImageOptions(
    val host: String,
    val port: Int,
    val memory: Long,
    val image: String,
    val environment: Map<String, Any>
) : ServerCompositionOptions