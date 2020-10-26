package me.devnatan.katan.common.impl.game

import me.devnatan.katan.api.game.GameImage

data class GameImageImpl(override val id: String, override val environment: Map<String, Any>) : GameImage