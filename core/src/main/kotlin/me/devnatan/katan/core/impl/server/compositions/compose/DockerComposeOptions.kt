package me.devnatan.katan.core.impl.server.compositions.compose

import me.devnatan.katan.api.server.ServerCompositionOptions

class DockerComposeOptions(
    val compose: String,
    val properties: Map<String, String>
) : ServerCompositionOptions