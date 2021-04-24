package me.devnatan.katan.core.impl.game

import com.typesafe.config.ConfigFactory
import com.typesafe.config.ConfigObject
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import me.devnatan.katan.api.game.Game
import me.devnatan.katan.api.game.GameManager
import me.devnatan.katan.api.logging.logger
import me.devnatan.katan.common.util.exportResource
import me.devnatan.katan.common.util.get
import me.devnatan.katan.common.util.getMap
import me.devnatan.katan.core.KatanCore
import java.io.File

class GameManagerImpl(private val core: KatanCore) : GameManager {

    companion object {
        private val log = logger<GameManager>()
    }

    private val games: MutableMap<String, Game> = HashMap()
    private val mutex = Mutex()

    internal suspend fun register() {
        val root = "games"
        val directory = File(root)
        if (!directory.exists())
            directory.mkdirs()

        // TODO: export games automatically
        exportResource("$root/minecraft.conf")

        for (file in directory.listFiles()?.filterNotNull() ?: emptyList()) {
            val config = ConfigFactory.parseFile(file)
            val gameName = config.get("name", file.nameWithoutExtension)
            val settings = config.getConfig("settings").let {
                GameSettingsImpl(
                    it.get("ports.min", 0)..it.get(
                        "ports.max",
                        Short.MAX_VALUE * 2 - 1
                    )
                );
            }

            val versions = config.getConfig("versions")
                .root().entries.map { (key, value) ->
                    val versionConfig = (value as ConfigObject).toConfig()
                    val versionName = versionConfig.get("name", key)
                    GameVersionImpl(
                        key,
                        versionName,
                        versionConfig.get("display-name", null),
                        versionConfig.get("image", null),
                        versionConfig.getMap("environment")
                    )
                }

            val game = GameImpl(
                config.getString("id"),
                gameName,
                config.get("display-name", null),
                settings,
                config.get("defaults.image", null),
                config.getMap("defaults.environment"),
                versions
            )

            log.info(
                if (game.versions.isEmpty())
                    core.translator.translate(
                        "katan.game-registered",
                        game.name
                    )
                else
                    core.translator.translate(
                        "katan.versioned-game-registered",
                        game.name,
                        game.versions.size
                    )
            )

            registerGame(game)
        }
    }

    override fun getRegisteredGames(): Collection<Game> {
        return games.values
    }

    override fun getGame(name: String): Game? {
        return games.entries.find { it.key.equals(name, true) }?.value
    }

    override fun isSupported(name: String): Boolean {
        return games.containsKey(name)
    }

    override suspend fun registerGame(game: Game) {
        mutex.withLock {
            games[game.name] = game
        }
    }

    override suspend fun unregisterGame(game: Game) {
        mutex.withLock {
            games.remove(game.name)
        }
    }

}