package org.katan.service.db

import org.apache.logging.log4j.LogManager
import org.jetbrains.exposed.sql.Database
import org.jetbrains.exposed.sql.DatabaseConfig
import org.jetbrains.exposed.sql.Slf4jSqlDebugLogger
import org.jetbrains.exposed.sql.transactions.transaction
import org.katan.config.KatanConfig

internal class PostgreSQLDatabaseServiceImpl(
    private val config: KatanConfig
) : DatabaseService {

    companion object {
        private val logger = LogManager.getLogger(PostgreSQLDatabaseServiceImpl::class.java)
    }

    override fun get(): Database {
        val url = "jdbc:${config.database.connectionString}"
        val conn = Database.connect(
            url = url,
            user = config.database.username,
            password = config.database.password,
            driver = "org.postgresql.Driver",
            databaseConfig = DatabaseConfig.invoke {
                sqlLogger = Slf4jSqlDebugLogger
            }
        )

        try {
            // try to establish initial connection before a transaction
            transaction { !conn.connector().isClosed }
        } catch (e: Throwable) {
            logger.error("Failed to establish database connection.", e)
        }

        return conn
    }
}
