package me.devnatan.katan.api.game

/**
 * Games are one of the main entities present in the Katan ecosystem, they
 * are used as an information base for the creation of a [Server].
 *
 * Each server must target a [Game], it will also serve as a direct manipulator
 * of the properties of that server in addition to serving as a limiter for
 * server environment variables.
 *
 * Games are dynamic and can be created, removed and manipulated at any
 * stage in the Katan process. New games can be added using the [GameManager].
 *
 * @author Natan Vieira
 * @since  1.0
 */
interface Game : GameVersion {

    val id: String

    /**
     * Returns the settings for that game. [Server]s targeting this game
     * should respect these settings overriding their own.
     */
    val settings: GameSettings

    /**
     * Returns the list of [GameVersion] available for this game.
     */
    val versions: List<GameVersion>

}