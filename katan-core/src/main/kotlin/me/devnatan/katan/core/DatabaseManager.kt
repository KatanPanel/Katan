package me.devnatan.katan.core

import com.typesafe.config.Config
import me.devnatan.katan.api.logging.logger
import me.devnatan.katan.common.EnvKeys
import me.devnatan.katan.common.util.getEnv
import me.devnatan.katan.common.util.getMap
import me.devnatan.katan.database.DatabaseConnector
import me.devnatan.katan.database.DatabaseFactory
import me.devnatan.katan.database.DatabaseQueryHandler
import me.devnatan.katan.database.DatabaseSettings
import me.devnatan.katan.database.jdbc.JDBCConnector
import org.slf4j.Logger
import kotlin.system.exitProcess
import kotlin.time.Duration
import kotlin.time.ExperimentalTime
import kotlin.time.measureTime

class DatabaseManager(private val core: KatanCore) {

    companion object {
        private val log: Logger = logger<DatabaseManager>()
    }

    @OptIn(ExperimentalTime::class)
    private object QueryHandler : DatabaseQueryHandler {
        override fun error(query: String, duration: Duration, error: Throwable) {
            log.trace("Query \"$query\" failed in ${duration.inSeconds}s.", error)
        }

        override fun completed(query: String, duration: Duration) {
            log.info("Query \"$query\" completed took ${duration.inSeconds}s.")
        }
    }

    private val factory = DatabaseFactory()
    private var _db: DatabaseConnector? = null

    val db: DatabaseConnector
        get() = _db ?: error("Not connected yet")

    init {
        factory.register(JDBCConnector(QueryHandler))
    }

    @OptIn(ExperimentalTime::class)
    internal suspend fun connect(config: Config) {
        check(_db != null) { "Database connector settings must be validated before connect" }

        val settings = validateConnectorSettings(config)

        log.info(core.translator.translate("katan.database.connecting"))

        runCatching {
            val time = measureTime {
                _db!!.connect(settings)
            }

            log.info(core.translator.translate("katan.database.connected", time.inSeconds))
        }.onFailure { error ->
            log.error(core.translator.translate("katan.database.fail"))
            log.trace(null, error)
            exitProcess(0)
        }
    }

    internal suspend fun close() {
        _db?.close()
    }

    private fun throwMissingProperty(property: String): Nothing {
        throw IllegalArgumentException("Missing \"$property\" database property")
    }

    private fun getOrMiss(config: Config, property: String, env: String): String {
        return config.getEnv(property, env) { throwMissingProperty("$property ($env)") }
    }

    private fun validateConnectorSettings(config: Config): DatabaseSettings {
        val url = config.getEnv("url", EnvKeys.DB_URL)

        return if (url != null) {
            _db = factory.fromUrl(url)
            DatabaseSettings(url)
        } else {
            val dialect = getOrMiss(config, "dialect", EnvKeys.DB_DIALECT)

            _db = factory.fromDialect(dialect)
            if (_db == null)
                throw IllegalArgumentException("Unsupported database dialect: $dialect")

            DatabaseSettings(
                null,
                dialect,
                getOrMiss(config, "host", EnvKeys.DB_HOST),
                getOrMiss(config, "credentials.user", EnvKeys.DB_USER),
                config.getEnv("credentials.password", EnvKeys.DB_PASSWORD, ""),
                getOrMiss(config, "database", EnvKeys.DB_NAME),
                config.getMap("properties")
            )
        }
    }

}