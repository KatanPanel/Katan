package me.devnatan.katan.core.impl.game

import me.devnatan.katan.api.game.Game
import me.devnatan.katan.api.game.GameManager
import me.devnatan.katan.api.game.GameType

class GameManagerImpl : GameManager {

    private val games: MutableMap<String, Game> = HashMap()

    override fun getSupportedGames(): List<GameType> {
        return synchronized(this) {
            games.values.map(Game::type)
        }
    }

    override fun getGame(name: String): Game? {
        return synchronized(this) {
            games.entries.find { it.key.equals(name, true) }?.value
        }
    }

    override fun isSupported(name: String): Boolean {
        return synchronized(this) {
            games.containsKey(name)
        }
    }

    override fun isNative(name: String): Boolean {
        return GameType.native(name) != null
    }

    override fun registerGame(game: Game) {
        synchronized(this) {
            games[game.type.name] = game
        }
    }

    override fun unregisterGame(game: Game) {
        synchronized(this) {
            games.remove(game.type.name)
        }
    }

}