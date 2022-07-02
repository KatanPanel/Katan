package org.katan.http.server

import io.ktor.server.application.Application
import io.ktor.server.cio.CIO
import io.ktor.server.engine.ApplicationEngine
import io.ktor.server.engine.EngineConnectorBuilder
import io.ktor.server.engine.addShutdownHook
import io.ktor.server.engine.embeddedServer
import kotlinx.atomicfu.atomic
import kotlinx.coroutines.CoroutineName
import kotlinx.coroutines.CoroutineScope
import org.apache.logging.log4j.LogManager
import org.apache.logging.log4j.Logger
import org.katan.http.HttpModuleRegistry
import org.katan.http.installDefaultFeatures
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject

class HttpServer(
    val host: String? = null,
    val port: Int
) : CoroutineScope by CoroutineScope(CoroutineName("HttpServer")), KoinComponent {

    private val httpModuleRegistry by inject<HttpModuleRegistry>()
    private val logger: Logger = LogManager.getLogger(HttpServer::class.java)

    init {
        System.setProperty("io.ktor.development", "true")
    }

    private var shutdownPending by atomic(false)

    private val engine: ApplicationEngine by lazy {
        embeddedServer(
            factory = CIO,
            module = { setupEngine(this) },
            connectors = arrayOf(createHttpConnector())
        )
    }

    fun start() {
        engine.addShutdownHook {
            stop()
        }

        logger.info("Listening on {}", port)
        engine.start(wait = true)
    }

    fun stop() {
        if (shutdownPending) return

        shutdownPending = true
        engine.stop(
            gracePeriodMillis = 1000,
            timeoutMillis = 5000
        )
        shutdownPending = false
    }

    private fun setupEngine(app: Application) {
        app.installDefaultFeatures()
        for (module in httpModuleRegistry) {
            module.install(app)
            logger.info("Module {} installed", module::class.simpleName)
        }
    }

    private fun createHttpConnector() = EngineConnectorBuilder().apply {
        host = this@HttpServer.host ?: "0.0.0.0"
        port = this@HttpServer.port
    }

}