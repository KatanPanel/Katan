package org.katan

import org.apache.logging.log4j.LogManager
import org.apache.logging.log4j.Logger
import org.katan.http.server.HttpServer
import org.koin.core.component.KoinComponent
import java.io.Closeable

class Katan : KoinComponent {

    companion object {
        private const val DEFAULT_PORT: Int = 40055
        val LOGGER: Logger = LogManager.getLogger(Katan::class.java)
    }

    private val port: Int by lazy { selectPort() }
    private val httpServer: HttpServer = HttpServer(port = port)

    fun start() {
        httpServer.start()
    }

    fun close() {
        httpServer.stop()
        LOGGER.info("Katan shutdown")
    }

    /**
     * Selects the port that serve will be run on from the environment variable
     * or use the [DEFAULT_PORT] if the port environment variable is not defined.
     */
    private fun selectPort(): Int {
        return System.getenv("PORT")?.toIntOrNull() ?: DEFAULT_PORT
    }

}