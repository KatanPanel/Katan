package me.devnatan.katan.webserver

import com.typesafe.config.ConfigFactory
import io.ktor.server.cio.*
import io.ktor.server.engine.*
import io.ktor.util.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.runBlocking
import me.devnatan.katan.api.manager.AccountManager
import me.devnatan.katan.api.manager.ServerManager
import me.devnatan.katan.common.util.exportResource
import me.devnatan.katan.common.util.get
import me.devnatan.katan.webserver.websocket.WebSocketManager
import org.slf4j.LoggerFactory
import java.security.KeyStore
import java.util.concurrent.TimeUnit
import kotlin.text.toCharArray

class KatanWebServer(
    val accountManager: AccountManager,
    val serverManager: ServerManager
) {

    companion object {
        lateinit var INSTANCE: KatanWebServer
            private set
        val logger = LoggerFactory.getLogger(KatanWebServer::class.java)!!
    }

    lateinit var engine: CIOApplicationEngine
    val config = ConfigFactory.load(ConfigFactory.parseFile(this::class.exportResource("webserver.conf")))!!
    val webSocketManager: WebSocketManager = WebSocketManager()
    val enabled = config.get("enabled", true)

    @OptIn(KtorExperimentalAPI::class)
    fun init() {
        INSTANCE = this
        logger.info("Configuring Web Server...")

        val deployment = config.getConfig("deployment")
        engine = embeddedServer(
            CIO,
            applicationEngineEnvironment {
                val ssl = deployment.getConfig("ssl")

                connector {
                    host = deployment.get("host", "0.0.0.0")
                    port = deployment.get("port", 80)
                }

                /* sslConnector(
                    KeyStore.getInstance(KeyStore.getDefaultType()),
                    ssl.getString("keyAlias"),
                    { ssl.getString("keyStorePassword").toCharArray() },
                    { ssl.getString("privateKeyPassword").toCharArray() }
                ) {} */

                module {
                    installHooks()
                    setupRouter()
                }
            }
        )

        logger.info("Starting Web Server...")
        engine.start()
    }

    suspend fun close() {
        logger.info("Closing Web Server...")
        webSocketManager.close()

        val shutdown = config.getConfig("deployment.shutdown")
        engine.stop(
            shutdown.get("gracePeriod", 1000),
            shutdown.get("timeout", 5000),
            TimeUnit.MILLISECONDS)
    }

}