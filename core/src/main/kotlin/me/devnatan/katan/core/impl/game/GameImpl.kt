package me.devnatan.katan.core.impl.game

import me.devnatan.katan.api.game.Game
import me.devnatan.katan.api.game.GameSettings
import me.devnatan.katan.api.game.GameType
import me.devnatan.katan.api.game.GameVersion

data class GameImpl(
    override val name: String,
    override val type: GameType,
    override val settings: GameSettings,
    override val image: String?,
    override val environment: Map<String, Any>,
    override val versions: Array<out GameVersion>
) : Game {

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is GameImpl) return false

        if (name != other.name) return false
        if (type != other.type) return false
        if (settings != other.settings) return false
        if (image != other.image) return false
        if (environment != other.environment) return false
        if (!versions.contentEquals(other.versions)) return false

        return true
    }

    override fun hashCode(): Int {
        var result = name.hashCode()
        result = 31 * result + type.hashCode()
        result = 31 * result + settings.hashCode()
        result = 31 * result + image.hashCode()
        result = 31 * result + environment.hashCode()
        result = 31 * result + versions.contentHashCode()
        return result
    }

}