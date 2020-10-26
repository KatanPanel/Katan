package me.devnatan.katan.api.game

/**
 * It represents a game supported by Katan, whether it is supported natively or added dynamically.
 * New games can be supported by being registered through [GameManager].
 */
interface Game {

    /**
     * Returns the game type.
     */
    val type: GameType

    /**
     * Returns the default settings that will be used for that game.
     */
    val settings: GameSettings

}