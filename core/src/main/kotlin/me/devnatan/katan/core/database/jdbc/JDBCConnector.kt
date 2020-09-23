package me.devnatan.katan.core.database.jdbc

import io.netty.handler.codec.http.QueryStringEncoder
import kotlinx.coroutines.Dispatchers
import me.devnatan.katan.common.replaceEach
import me.devnatan.katan.core.database.DatabaseConnector
import me.devnatan.katan.core.database.DatabaseSettings
import me.devnatan.katan.core.database.jdbc.entity.AccountsTable
import me.devnatan.katan.core.database.jdbc.entity.ServerHoldersTable
import me.devnatan.katan.core.database.jdbc.entity.ServersTable
import org.jetbrains.exposed.sql.Database
import org.jetbrains.exposed.sql.SchemaUtils
import org.jetbrains.exposed.sql.transactions.experimental.newSuspendedTransaction
import org.slf4j.LoggerFactory

abstract class JDBCConnector<S : JDBCSettings>(
    override val name: String,
    override val driver: String,
    override val url: String,
) : DatabaseConnector<S> {

    companion object {
        val logger = LoggerFactory.getLogger(JDBCConnector::class.java)!!
    }

    lateinit var database: Database

    override fun close() {
        database.connector().close()
    }

}

open class JDBCRemoteConnector(
    name: String,
    driver: String,
    url: String,
) : JDBCConnector<JDBCRemoteSettings>(name, driver, url) {

    override suspend fun connect(settings: JDBCRemoteSettings) {
        logger.info("Connecting to ${settings.host}...")
        database = Database.connect(
            createConnectionUrl(settings),
            this.driver,
            settings.user,
            settings.password
        )

        try {
            newSuspendedTransaction(Dispatchers.Default, database) {
                SchemaUtils.create(
                    AccountsTable,
                    ServersTable,
                    ServerHoldersTable
                )
            }
            logger.info("Connected successfully!")
        } catch (e: Throwable) {
            logger.error("Couldn't connect to database, please check your credentials and try again.")
            logger.error("{}", e.toString())
            throw e
        }
    }

    override fun createConnectionUrl(settings: JDBCRemoteSettings): String {
        return defaultConnectionUrl(settings)
    }

}

open class JDBCLocalConnector(
    name: String,
    driver: String,
    url: String,
) : JDBCConnector<JDBCLocalSettings>(name, driver, url) {

    override suspend fun connect(settings: JDBCLocalSettings) {
        throw UnsupportedOperationException()
    }

    override fun createConnectionUrl(settings: JDBCLocalSettings): String {
        return defaultConnectionUrl(settings)
    }

}

private fun <S : JDBCSettings> JDBCConnector<S>.defaultConnectionUrl(settings: S): String {
    return when (settings) {
        is JDBCRemoteSettings -> {
            QueryStringEncoder(url.replaceEach {
                "{host}" by settings.host
                "{database}" by settings.database
            }).apply {
                for ((name, value) in settings.connectionProperties.entries) {
                    addParam(name, value)
                }
            }.toString()
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
    override val connectionProperties: Map<String, String>,
) : JDBCSettings

open class JDBCLocalSettings(
    val file: String,
    override val connectionProperties: Map<String, String>,
) : JDBCSettings
