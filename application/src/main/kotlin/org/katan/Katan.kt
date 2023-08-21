package org.katan

import org.apache.logging.log4j.LogManager
import org.apache.logging.log4j.Logger
import org.jetbrains.exposed.sql.Database
import org.jetbrains.exposed.sql.transactions.experimental.newSuspendedTransaction
import org.katan.config.KatanConfig
import org.katan.http.server.HttpServer
import org.koin.core.component.KoinComponent
import org.koin.core.component.get
import org.koin.core.component.inject
import java.net.ConnectException
import java.sql.SQLException
import kotlin.system.exitProcess

internal class Katan : KoinComponent {

    private companion object {

        private val logger: Logger = LogManager.getLogger(Katan::class.java)
    }

    private val config: KatanConfig by inject()
    private lateinit var httpServer: HttpServer

    suspend fun start() {
        checkDatabaseConnection()
        httpServer = HttpServer(config.host, config.port)
        httpServer.start()
    }

    private suspend fun checkDatabaseConnection() {
        val database = get<Database>()
        try {
            newSuspendedTransaction(db = database) {
                database.connector()
            }
        } catch (exception: SQLException) {
            if (config.isDevelopment)
                logger.error("Unable to establish database connection.", exception)
            else
                logger.debug("Unable to establish database connection: {}", exception.message)

            exitProcess(0)
        }
    }

    internal fun close() {
        if (!::httpServer.isInitialized)
            return

        httpServer.stop()
    }
}
