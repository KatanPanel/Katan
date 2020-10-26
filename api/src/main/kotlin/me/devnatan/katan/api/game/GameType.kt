package me.devnatan.katan.api.game

private const val MINECRAFT_GAME = "Minecraft"

/**
 * Represents all games currently supported by Katan.
 * @property name the game name.
 */
abstract class GameType(val name: String) {

    companion object {

        /**
         * Returns a game supported natively by Katan that has the specified
         * [name] or `null` if it is not native or is not supported.
         */
        @JvmStatic
        fun native(name: String): GameType? {
            return when (name) {
                MINECRAFT_GAME -> MinecraftGame
                else -> null
            }
        }

    }

    override fun toString(): String {
        return name
    }

}

/**
 * Native support for the game "Minecraft".
 * See: https://minecraft.net
 */
object MinecraftGame : GameType(MINECRAFT_GAME)