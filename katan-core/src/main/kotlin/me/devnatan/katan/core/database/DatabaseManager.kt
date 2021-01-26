package me.devnatan.katan.core.database

import com.typesafe.config.Config
import me.devnatan.katan.common.util.get
import me.devnatan.katan.core.KatanCore
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import kotlin.system.measureTimeMillis

class DatabaseManager(private val core: KatanCore) {

    companion object {
        private val logger: Logger = LoggerFactory.getLogger(DatabaseManager::class.java)
    }

    lateinit var database: DatabaseConnector

    suspend fun connect() {
        val db = core.config.getConfig("database")
        val dialect = db.get("source", KatanCore.DATABASE_DIALECT_FALLBACK)
        logger.info(core.translator.translate("katan.database.dialect", dialect, KatanCore.DATABASE_DIALECT_FALLBACK))

        val dialectSettings = runCatching {
            db.getConfig(dialect.toLowerCase())
        }.onFailure {
            throw IllegalArgumentException("Dialect properties not found: $dialect.")
        }.getOrThrow()

        connectWith(dialect, dialectSettings, db.get("strict", false))
    }

    private suspend fun connectWith(dialect: String, config: Config, strict: Boolean) {
        val dialectName = dialect.toLowerCase()
        if (!SUPPORTED_CONNECTORS.containsKey(dialectName))
            throw IllegalArgumentException("Unsupported database dialect: $dialect")

        val (connector, settings) = SUPPORTED_CONNECTORS.getValue(dialectName).invoke(config)
        try {
            database = connector
            logger.info(core.translator.translate(if(settings is LocalDatabaseSettings) "katan.database.locally-connecting" else "katan.database.connecting", settings.toString()))

            val took = measureTimeMillis {
                connector.connect(settings)
            }
            logger.info(core.translator.translate("katan.database.connected", String.format("%.2f", took / 1000.0f)))
        } catch (e: Throwable) {
            logger.error(core.translator.translate("katan.database.fail", dialect))
            if (strict || dialect.equals(KatanCore.DATABASE_DIALECT_FALLBACK, true))
                throw e

            logger.info(core.translator.translate("katan.database.strict", KatanCore.DATABASE_DIALECT_FALLBACK))
            connectWith(KatanCore.DATABASE_DIALECT_FALLBACK, config, strict)
        }
    }

}