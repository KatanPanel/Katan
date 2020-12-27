package me.devnatan.katan.common.impl.server

import me.devnatan.katan.api.game.GameType
import me.devnatan.katan.api.game.GameVersion
import me.devnatan.katan.api.server.ServerGame

data class ServerGameImpl(
    override val type: GameType,
    override val version: GameVersion?
) : ServerGame {

    override fun toString(): String {
        return "${type.name}${version?.let {
            " (${it.name})"
        }}"
    }

}