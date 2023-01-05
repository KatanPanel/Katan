package org.katan

import kotlinx.coroutines.runBlocking
import org.apache.logging.log4j.LogManager
import org.apache.logging.log4j.Logger
import org.katan.config.KatanConfig
import org.katan.http.server.HttpServer
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject

class Katan : KoinComponent {

    private val logger: Logger = LogManager.getLogger(Katan::class.java)
    private val config: KatanConfig by inject()
    private val httpServer: HttpServer = HttpServer(
        host = config.host,
        port = config.port
    )

    fun start() {
        setupShutdownHook()
        httpServer.start()

        // TODO check for some services availability, database connectivity, etc
    }

    private fun setupShutdownHook() {
        Runtime.getRuntime().addShutdownHook(
            Thread {
                close()
            }
        )
    }

    private fun close() {
        runBlocking { httpServer.stop() }
        logger.info("Done. Bye bye :)")
    }
}
