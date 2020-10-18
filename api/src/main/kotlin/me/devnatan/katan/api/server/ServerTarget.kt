package me.devnatan.katan.api.server

/**
 * Represents all games currently supported by Katan.
 */
enum class ServerTarget(val game: String) {

    MINECRAFT("Minecraft"),
    UNKNOWN("Unknown");

    companion object {

        /**
         * Returns a supported [game] from its name (case-insensitive) or [UNKNOWN] if not found.
         */
        @JvmStatic
        fun byGame(game: String): ServerTarget {
            return values().firstOrNull {
                it.game.equals(game, true)
            } ?: UNKNOWN
        }

    }

    override fun toString(): String {
        return game
    }

}