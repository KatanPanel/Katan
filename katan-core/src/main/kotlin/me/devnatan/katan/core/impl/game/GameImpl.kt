package me.devnatan.katan.core.impl.game

import me.devnatan.katan.api.game.Game
import me.devnatan.katan.api.game.GameSettings
import me.devnatan.katan.api.game.GameVersion

data class GameImpl(
    override val id: String,
    override val name: String,
    override val displayName: String?,
    override val settings: GameSettings,
    override val image: String?,
    override val environment: Map<String, Any>,
    override val versions: List<GameVersion>
) : Game