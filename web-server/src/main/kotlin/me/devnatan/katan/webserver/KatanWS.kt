package me.devnatan.katan.webserver

import com.typesafe.config.Config
import com.typesafe.config.ConfigFactory
import io.ktor.server.engine.*
import io.ktor.server.jetty.*
import io.ktor.util.*
import me.devnatan.katan.api.Katan
import me.devnatan.katan.common.util.exportResource
import me.devnatan.katan.common.util.get
import org.slf4j.LoggerFactory
import java.util.concurrent.TimeUnit

class KatanWS(val katan: Katan) {

    companion object {
        val logger = LoggerFactory.getLogger(KatanWS::class.java)!!
    }

    lateinit var server: ApplicationEngine
    lateinit var environment: Environment
    val config: Config = ConfigFactory.load(ConfigFactory.parseFile(exportResource("webserver.conf")))!!
    lateinit var internalAccountManager: TokenManager
    val enabled = config.get("enabled", false)

    val accountManager get() = katan.accountManager
    val serverManager get() = katan.serverManager

    @OptIn(KtorExperimentalAPI::class)
    fun init() {
        logger.info("Creating environment...")
        internalAccountManager = TokenManager(this)
        environment = Environment(this)
        environment.start()
        server = embeddedServer(Jetty, environment.environment)
        logger.info("Starting server...")
        server.start()
    }

    suspend fun close() {
        if (!::environment.isInitialized) return
        logger.info("Closing environment...")
        environment.close()

        logger.info("Shutting down the server...")
        val shutdown = config.getConfig("deployment.shutdown")
        server.stop(
            shutdown.get("gracePeriod", 1000),
            shutdown.get("timeout", 5000),
            TimeUnit.MILLISECONDS
        )
    }

}