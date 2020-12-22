package me.devnatan.katan.core.database.jdbc

import kotlinx.coroutines.Dispatchers
import me.devnatan.katan.common.util.replaceEach
import me.devnatan.katan.core.database.DatabaseConnector
import me.devnatan.katan.core.database.DatabaseSettings
import me.devnatan.katan.core.database.LocalDatabaseSettings
import me.devnatan.katan.core.database.jdbc.entity.AccountsTable
import me.devnatan.katan.core.database.jdbc.entity.ServerCompositionsTable
import me.devnatan.katan.core.database.jdbc.entity.ServerHoldersTable
import me.devnatan.katan.core.database.jdbc.entity.ServersTable
import org.jetbrains.exposed.sql.Database
import org.jetbrains.exposed.sql.SchemaUtils
import org.jetbrains.exposed.sql.transactions.experimental.newSuspendedTransaction

abstract class JDBCConnector(
    override val name: String,
    override val driver: String,
    override val url: String
) : DatabaseConnector {

    lateinit var database: Database

    override suspend fun connect(settings: DatabaseSettings) {
        database = if (settings is JDBCRemoteSettings) {
            Database.connect(
                createConnectionUrl(settings),
                this.driver,
                settings.user,
                settings.password
            )
        } else Database.connect(
            createConnectionUrl(settings),
            this.driver,
        )

        newSuspendedTransaction(Dispatchers.Default, database) {
            SchemaUtils.create(
                AccountsTable,
                ServersTable,
                ServerHoldersTable,
                ServerCompositionsTable
            )
        }
    }

    override fun close() {
        database.connector().close()
    }

    override fun createConnectionUrl(settings: DatabaseSettings): String {
        return defaultConnectionUrl(settings)
    }

}

private fun JDBCConnector.defaultConnectionUrl(settings: DatabaseSettings): String {
    return when (settings) {
        is JDBCRemoteSettings -> {
            url.replaceEach {
                "{host}" by settings.host
                "{database}" by settings.database
            } + buildString {
                val properties = settings.connectionProperties
                if (properties.isNotEmpty()) {
                    append("?")
                    append(properties.map {
                        "${it.key}=${it.value}"
                    }.joinToString("&"))
                }
            }
        }
        is JDBCLocalSettings -> {
            url.replaceEach {
                "{file}" by settings.file
            }
        }
        else -> throw UnsupportedOperationException()
    }
}

interface JDBCSettings : DatabaseSettings {

    val connectionProperties: Map<String, String>

}

open class JDBCRemoteSettings(
    val host: String,
    val user: String,
    val password: String,
    val database: String,
    override val connectionProperties: Map<String, String>
) : JDBCSettings {

    override fun toString(): String {
        return "$host -> $database ($user)"
    }

}

open class JDBCLocalSettings(
    val file: String,
    override val connectionProperties: Map<String, String>,
) : JDBCSettings, LocalDatabaseSettings {

    override fun toString(): String {
        return file
    }

}