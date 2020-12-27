package me.devnatan.katan.core.impl.game

import com.typesafe.config.ConfigFactory
import com.typesafe.config.ConfigObject
import me.devnatan.katan.api.game.Game
import me.devnatan.katan.api.game.GameManager
import me.devnatan.katan.api.game.GameType
import me.devnatan.katan.common.util.exportResource
import me.devnatan.katan.common.util.get
import me.devnatan.katan.common.util.getMap
import me.devnatan.katan.core.KatanCore
import me.devnatan.katan.core.database.DatabaseManager
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import java.io.File

class GameManagerImpl(core: KatanCore) : GameManager {

    companion object {
        private val logger: Logger = LoggerFactory.getLogger(DatabaseManager::class.java)
    }

    private val games: MutableMap<String, Game> = HashMap()

    init {
        val root = "games"
        val directory = File(root)
        if (!directory.exists())
            directory.mkdirs()

        for (supported in GameType.supported) {
            exportResource("$root/${supported.name.toLowerCase()}.conf")
        }

        for (file in directory.listFiles()?.filterNotNull() ?: emptyList()) {
            val config = ConfigFactory.parseFile(file)
            val gameName = config.get("name", file.nameWithoutExtension)
            val settings = config.getConfig("settings").let {
                GameSettingsImpl(it.get("ports.min", 0)..it.get("ports.max", Short.MAX_VALUE * 2 - 1));
            }

            val versions = config.getConfig("versions").root().entries.map { (key, value) ->
                val version = (value as ConfigObject).toConfig()
                GameVersionImpl(version.get("name", key),
                    version.get("image", null),
                    version.getMap("environment"))
            }.toTypedArray()

            // TODO: check for non-native game type
            val game = GameImpl(
                gameName,
                GameType.native(gameName)!!,
                settings,
                config.get("defaults.image", null),
                config.getMap("defaults.environment"),
                versions
            )

            logger.info(
                if (game.versions.isEmpty())
                    core.translator.translate("katan.game-registered", game.name)
                else
                    core.translator.translate("katan.versioned-game-registered", game.name, game.versions.size)
            )

            registerGame(game)
        }
    }

    override fun getSupportedGames(): List<GameType> {
        return games.values.map(Game::type)
    }

    override fun getRegisteredGames(): Collection<Game> {
        return games.values
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