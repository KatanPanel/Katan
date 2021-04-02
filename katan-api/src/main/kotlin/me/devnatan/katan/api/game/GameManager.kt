package me.devnatan.katan.api.game

/**
 * It represents the game handler of Katan, through it it is possible to register
 * new [Game]s making the range of games supported by Katan beyond the native ones grow.
 */
interface GameManager {

    /**
     * Returns all supported games.
     */
    fun getRegisteredGames(): Collection<Game>

    /**
     * Returns a supported [Game] from its [name] (case-insensitive) or `null` if not found.
     * @param name the game name.
     */
    fun getGame(name: String): Game?

    /**
     * Returns whether the game with the specified [name] is supported by Katan.
     * @param name the game name.
     */
    fun isSupported(name: String): Boolean

    /**
     * Returns whether the specified game type is supported natively by Katan.
     * @param name the game name.
     */
    fun isNative(name: String): Boolean

    /**
     * Register a new game.
     * @param game the game to be registered.
     */
    fun registerGame(game: Game)

    /**
     * Unregisters a game.
     * @param game the game to be unregistered.
     */
    fun unregisterGame(game: Game)

}