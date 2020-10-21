package me.devnatan.katan.core.impl.server.compositions.compose

import me.devnatan.katan.api.annotations.UnstableKatanApi
import me.devnatan.katan.api.server.ServerCompositionOptions

@OptIn(UnstableKatanApi::class)
class DockerComposeOptions(
    val compose: String,
    val properties: Map<String, String>
) : ServerCompositionOptions