package me.devnatan.katan.common.impl.server

import me.devnatan.katan.api.game.Game
import me.devnatan.katan.api.game.GameVersion
import me.devnatan.katan.api.server.ServerGame

data class ServerGameImpl(
    override val game: Game,
    override val version: GameVersion?
) : ServerGame {

    override fun toString(): String {
        return "${game.displayName}${version?.let {
            " (${it.name})"
        }}"
    }

}