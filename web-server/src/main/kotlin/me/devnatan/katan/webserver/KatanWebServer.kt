package me.devnatan.katan.webserver

import com.typesafe.config.ConfigFactory
import io.ktor.server.cio.*
import io.ktor.server.engine.*
import io.ktor.server.netty.*
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

    lateinit var engine: ApplicationEngine
    val config = ConfigFactory.load(ConfigFactory.parseFile(this::class.exportResource("webserver.conf")))!!
    val webSocketManager: WebSocketManager = WebSocketManager()
    val enabled = config.get("enabled", true)

    @OptIn(KtorExperimentalAPI::class)
    fun init() {
        INSTANCE = this
        logger.info("Configuring Web Server...")

        val deployment = config.getConfig("deployment")
        engine = embeddedServer(
            Netty,
            applicationEngineEnvironment {
                connector {
                    host = deployment.get("host", "0.0.0.0")
                    port = deployment.get("port", 80)
                }

                if (deployment.hasPath("sslPort")) {
                    val ssl = deployment.getConfig("ssl")
                    val ks = KeyStore.getInstance(KeyStore.getDefaultType())
                    val pass = ssl.getString("keyStorePassword").toCharArray()
                    ks.load(null, pass)

                    sslConnector(
                        ks,
                        ssl.getString("keyAlias"),
                        { pass },
                        { ssl.getString("privateKeyPassword").toCharArray() }
                    ) {}
                }

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