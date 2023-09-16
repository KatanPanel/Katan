package org.katan.service.db

import org.jetbrains.exposed.sql.Database
import org.jetbrains.exposed.sql.DatabaseConfig
import org.jetbrains.exposed.sql.Slf4jSqlDebugLogger
import org.katan.KatanConfig

internal class PostgresDatabaseService(private val config: KatanConfig) : DatabaseService {

    companion object {
        private const val URL_FORMAT = "jdbc:postgresql://%s/"
        private const val DRIVER = "org.postgresql.Driver"
        private const val DEFAULT_USERNAME = "postgres"
        private const val DEFAULT_PASSWORD = "postgres"
    }

    override fun get(): Database = Database.connect(
        url = URL_FORMAT.format(config.databaseHost),
        user = config.databaseUser.ifEmpty { DEFAULT_USERNAME },
        password = config.databasePassword.ifEmpty { DEFAULT_PASSWORD },
        driver = DRIVER,
        databaseConfig = DatabaseConfig.invoke {
            useNestedTransactions = true
            if (config.isDevelopment) {
                sqlLogger = Slf4jSqlDebugLogger
            }
        }
    )
}
