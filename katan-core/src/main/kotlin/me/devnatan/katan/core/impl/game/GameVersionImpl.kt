package me.devnatan.katan.core.impl.game

import me.devnatan.katan.api.game.GameVersion

data class GameVersionImpl(
    override val id: String,
    override val name: String,
    override val displayName: String?,
    override val image: String?,
    override val environment: Map<String, Any>
) : GameVersion