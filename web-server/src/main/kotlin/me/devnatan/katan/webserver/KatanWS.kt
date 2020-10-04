package me.devnatan.katan.webserver

import com.typesafe.config.Config
import com.typesafe.config.ConfigFactory
import io.ktor.server.engine.*
import io.ktor.server.jetty.*
import io.ktor.util.*
import me.devnatan.katan.api.Katan
import me.devnatan.katan.common.util.exportResource
import me.devnatan.katan.common.util.get
import me.devnatan.katan.webserver.environment.Environment
import org.slf4j.LoggerFactory
import java.util.concurrent.TimeUnit

class KatanWS(val katan: Katan) {

    companion object {
        val logger = LoggerFactory.getLogger(KatanWS::class.java)!!

        const val ACCOUNT_TOKEN_PREFIX = "account-token:"

    }

    lateinit var server: ApplicationEngine
    lateinit var environment: Environment
    lateinit var config: Config
    val accountManager get() = katan.accountManager
    lateinit var internalAccountManager: WSAccountManager

    private var _enabled = false
    val enabled: Boolean
        get() {
            load()
            _enabled = config.get("enabled", true)
            return _enabled
        }

    @OptIn(KtorExperimentalAPI::class)
    fun init() {
        load()
        logger.info("Creating environment...")
        environment = Environment(this)
        environment.start()
        server = embeddedServer(Jetty, environment.environment) {
            configureServer = {
                isDumpAfterStart = false
            }
        }
        logger.info("Starting server...")
        server.start()
    }


    private fun load() {
        if (!::config.isInitialized)
            config = ConfigFactory.load(ConfigFactory.parseFile(exportResource("webserver.conf")))!!
        internalAccountManager = WSAccountManager(this)
    }

    suspend fun close() {
        logger.info("Closing environment...")
        environment.close()

        logger.info("Stopping server...")
        val shutdown = config.getConfig("deployment.shutdown")
        server.stop(
            shutdown.get("gracePeriod", 1000),
            shutdown.get("timeout", 5000),
            TimeUnit.MILLISECONDS
        )
    }

}