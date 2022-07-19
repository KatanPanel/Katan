package org.katan

import org.apache.logging.log4j.LogManager
import org.apache.logging.log4j.Logger
import org.katan.config.KatanConfig
import org.katan.http.server.HttpServer
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject

class Katan : KoinComponent {

    companion object {
        val LOGGER: Logger = LogManager.getLogger(Katan::class.java)
    }

    private val config: KatanConfig by inject()
    private val httpServer: HttpServer by lazy {
        HttpServer(config.server.port)
    }

    fun start() {
        httpServer.start()
    }

    fun close() {
        httpServer.stop()
        LOGGER.info("Katan shutdown")
    }

}