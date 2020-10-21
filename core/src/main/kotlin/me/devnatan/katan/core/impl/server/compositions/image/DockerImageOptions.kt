package me.devnatan.katan.core.impl.server.compositions.image

import me.devnatan.katan.api.annotations.UnstableKatanApi
import me.devnatan.katan.api.server.ServerCompositionOptions

@OptIn(UnstableKatanApi::class)
class DockerImageOptions(var image: String) : ServerCompositionOptions