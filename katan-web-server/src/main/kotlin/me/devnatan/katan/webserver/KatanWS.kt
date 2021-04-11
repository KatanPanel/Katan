package me.devnatan.katan.webserver

import com.typesafe.config.Config
import com.typesafe.config.ConfigFactory
import io.ktor.server.engine.*
import io.ktor.server.jetty.*
import me.devnatan.katan.common.util.exportResource
import me.devnatan.katan.common.util.get
import me.devnatan.katan.core.KatanCore
import me.devnatan.katan.webserver.websocket.WebSocketManager
import me.devnatan.katan.webserver.websocket.handler.WebSocketServerHandler
import java.security.KeyStore
import java.util.concurrent.TimeUnit

class KatanWS(val katan: KatanCore, val config: Config) {

    companion object Initializer {

        @JvmStatic
        fun create(katan: KatanCore): KatanWS? {
            val config =
                ConfigFactory.load(ConfigFactory.parseFile(exportResource("webserver.conf")))!!

            return if (config.get("enabled", true))
                KatanWS(katan, config)
            else
                null
        }

    }

    val tokenManager: TokenManager = TokenManager(this)
    val webSocketManager = WebSocketManager(katan.eventBus)
    private val server: ApplicationEngine = embeddedServer(Jetty, setupEngine())

    init {
        webSocketManager.registerEventHandler(
            WebSocketServerHandler(katan)
        )
    }

    private fun setupEngine() = applicationEngineEnvironment {
        module {
            installFeatures(this@KatanWS)
            router(this@KatanWS)
        }

        val deploy = this@KatanWS.config.getConfig("deployment")
        connector {
            host = deploy.get("host", "localhost")
            port = deploy.get("port", 80)
            log.debug("HTTP connector available at: $host:$port")
        }

        if (deploy.get("ssl.enabled", false)) {
            val ssl = deploy.getConfig("ssl")
            val ks = KeyStore.getInstance(KeyStore.getDefaultType())
            val pass = ssl.getString("key-store-password").toCharArray()
            ks.load(null, pass)

            sslConnector(
                ks,
                ssl.getString("key-alias"),
                { pass },
                { ssl.getString("private-key-password").toCharArray() }
            ) {
                port = ssl.get("port", 443)
                log.debug("HTTPS connector available at: $host:$port")
            }
        }
    }

    fun start() {
        webSocketManager.listen()
        server.start()
    }

    suspend fun close() {
        webSocketManager.close()

        val shutdown = config.getConfig("deployment.shutdown")
        server.stop(
            shutdown.get("grace-period", 1000),
            shutdown.get("timeout", 5000),
            TimeUnit.MILLISECONDS
        )
    }

}