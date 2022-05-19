package org.katan.http

import io.ktor.server.engine.ApplicationEngine
import io.ktor.server.engine.ApplicationEngineEnvironment
import io.ktor.server.engine.addShutdownHook
import io.ktor.server.engine.applicationEngineEnvironment
import io.ktor.server.engine.connector
import io.ktor.server.engine.embeddedServer
import io.ktor.server.netty.Netty

class HttpServer(
    val host: String? = null,
    val port: Int
) {

    private val engine: ApplicationEngine = embeddedServer(Netty, setupEngine())

    private fun setupEngine(): ApplicationEngineEnvironment {
        val httpServer = this
        return applicationEngineEnvironment {
            module {
                installFeatures(httpServer)
                createRouter()
            }

            connector {
                host = httpServer.host ?: "0.0.0.0"
                port = httpServer.port
            }
        }
    }

    fun start() {
        engine.start(wait = true)
        engine.addShutdownHook {
            stop()
        }
    }

    fun stop() {
        engine.stop(
            gracePeriodMillis = 1000,
            timeoutMillis = 5000
        )
    }

}