package org.katan.service.db

import org.apache.logging.log4j.LogManager
import org.jetbrains.exposed.sql.Database
import org.jetbrains.exposed.sql.DatabaseConfig
import org.jetbrains.exposed.sql.Slf4jSqlDebugLogger
import org.jetbrains.exposed.sql.transactions.transaction
import org.katan.config.KatanConfig
import java.sql.SQLException

internal class PostgreSQLDatabaseServiceImpl(
    private val config: KatanConfig
) : DatabaseService {

    companion object {
        private const val URL_FORMAT = "jdbc:postgresql://%s/"
        private const val DRIVER = "org.postgresql.Driver"
        private const val DEFAULT_USERNAME = "postgres"
        private const val DEFAULT_PASSWORD = "postgres"

        private val logger = LogManager.getLogger(PostgreSQLDatabaseServiceImpl::class.java)
    }

    override fun get(): Database {
        val conn = Database.connect(
            url = URL_FORMAT.format(config.databaseHost),
            user = config.databaseUser.ifEmpty { DEFAULT_USERNAME },
            password = config.databasePassword.ifEmpty { DEFAULT_PASSWORD },
            driver = DRIVER,
            databaseConfig = DatabaseConfig.invoke {
                sqlLogger = Slf4jSqlDebugLogger
                useNestedTransactions = true
            }
        )

        try {
            // try to establish initial connection before a transaction
            transaction { !conn.connector().isClosed }
        } catch (e: SQLException) {
            logger.error("Failed to establish database connection.", e)
        }

        return conn
    }
}
