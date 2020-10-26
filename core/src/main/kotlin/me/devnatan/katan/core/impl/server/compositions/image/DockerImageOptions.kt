package me.devnatan.katan.core.impl.server.compositions.image

import me.devnatan.katan.api.game.GameImage
import me.devnatan.katan.api.server.ServerCompositionOptions

class DockerImageOptions(
    val host: String,
    val port: Int,
    val memory: Long,
    val image: GameImage
) : ServerCompositionOptions