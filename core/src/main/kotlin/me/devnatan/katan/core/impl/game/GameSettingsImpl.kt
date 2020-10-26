package me.devnatan.katan.core.impl.game

import me.devnatan.katan.api.game.GameImage
import me.devnatan.katan.api.game.GameSettings

data class GameSettingsImpl(override val image: GameImage, override val ports: IntRange) : GameSettings