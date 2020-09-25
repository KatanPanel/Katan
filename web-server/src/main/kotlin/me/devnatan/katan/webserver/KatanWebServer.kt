package me.devnatan.katan.webserver

import com.typesafe.config.ConfigFactory
import io.ktor.server.cio.*
import io.ktor.server.engine.*
import me.devnatan.katan.api.manager.AccountManager
import me.devnatan.katan.api.manager.ServerManager
import me.devnatan.katan.common.util.exportResource
import me.devnatan.katan.common.util.get
import me.devnatan.katan.webserver.websocket.WebSocketManager
import org.slf4j.LoggerFactory
import java.util.concurrent.TimeUnit

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

    fun init() {
        INSTANCE = this
        logger.info("Starting Web Server...")
        val deployment = config.getConfig("deployment")
        engine = embeddedServer(
            CIO,
            deployment.get("port", 80),
            deployment.get("host", "0.0.0.0")
        ) {
            installHooks()
            setupRouter()
        }
        engine.start(wait = true)
    }

    suspend fun close() {
        webSocketManager.close()
        engine.stop(10, 10, TimeUnit.SECONDS)
    }

}