package me.devnatan.katan.core.impl.game

import me.devnatan.katan.api.game.Game
import me.devnatan.katan.api.game.GameSettings
import me.devnatan.katan.api.game.GameType

data class GameImpl(
    override val type: GameType,
    override val settings: GameSettings
) : Game