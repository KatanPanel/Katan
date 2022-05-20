package org.katan.http

import io.ktor.server.application.Application
import io.ktor.server.engine.ApplicationEngine
import io.ktor.server.engine.EngineConnectorBuilder
import io.ktor.server.engine.addShutdownHook
import io.ktor.server.engine.embeddedServer
import io.ktor.server.netty.Netty
import kotlinx.atomicfu.atomic
import kotlinx.coroutines.CoroutineName
import kotlinx.coroutines.CoroutineScope
import org.katan.http.module.server.ServerModule

class HttpServer(
    val host: String? = null,
    val port: Int
) : CoroutineScope by CoroutineScope(CoroutineName("HttpServer")) {

    init {
        System.setProperty("io.ktor.development", "true")
    }

    private var shutdownPending by atomic(false)

    private val engine: ApplicationEngine = embeddedServer(
        factory = Netty,
        module = { setupEngine() },
        connectors = arrayOf(createHttpConnector())
    )

    private fun Application.setupEngine() {
        installDefaultServerFeatures()
        registerModules()
    }

    private fun createHttpConnector() = EngineConnectorBuilder().apply {
        host = this@HttpServer.host ?: "0.0.0.0"
        port = this@HttpServer.port
    }

    private fun setupSsl() {
    }

    private fun Application.registerModules() {
        ServerModule()
    }

    fun start() {
        engine.start(wait = true)
        engine.addShutdownHook {
            stop()
        }
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

}