package me.devnatan.katan.api.game

/**
 * Represents a game supported by Katan, whether it is supported natively or added dynamically.
 */
interface Game : GameVersion {

    /**
     * Returns the game type.
     */
    val type: GameType

    /**
     * Returns the settings that will be used for that game.
     */
    val settings: GameSettings

    /**
     * Returns the list of [GameVersion] available for this game.
     */
    val versions: Array<out GameVersion>

}
