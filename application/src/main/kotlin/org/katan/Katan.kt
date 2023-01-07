package org.katan

import kotlinx.coroutines.runBlocking
import org.apache.logging.log4j.LogManager
import org.apache.logging.log4j.Logger
import org.jetbrains.exposed.sql.Database
import org.jetbrains.exposed.sql.transactions.experimental.newSuspendedTransaction
import org.katan.config.KatanConfig
import org.katan.http.server.HttpServer
import org.koin.core.component.KoinComponent
import org.koin.core.component.get
import org.koin.core.component.inject
import kotlin.system.exitProcess
import kotlin.time.DurationUnit
import kotlin.time.ExperimentalTime
import kotlin.time.measureTime

internal class Katan : KoinComponent {

    companion object {
        private val logger: Logger = LogManager.getLogger(Katan::class.java)
    }

    private val config: KatanConfig by inject()
    private val httpServer: HttpServer = HttpServer(
        host = config.host,
        port = config.port
    )

    suspend fun start() {
        checkDatabaseConnection()
        httpServer.start()
    }

    private suspend fun checkDatabaseConnection() {
        val database = get<Database>()

        // try to establish initial connection before a transaction
        @OptIn(ExperimentalTime::class)
        val duration = measureTime {
            newSuspendedTransaction(db = database) {
                runCatching {
                    database.connector()
                }.onFailure { exception ->
                    logger.error("Unable to establish database connection.", exception)
                    exitProcess(0)
                }
            }
        }

        logger.debug(
            "Database connection established took {}.",
            duration.toString(DurationUnit.MILLISECONDS)
        )
    }

    internal fun close() {
        runBlocking {
            httpServer.stop()
        }
    }
}
