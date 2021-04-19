package me.devnatan.katan.webserver

import com.typesafe.config.Config
import com.typesafe.config.ConfigFactory
import io.ktor.server.engine.*
import io.ktor.server.netty.*
import io.ktor.util.*
import me.devnatan.katan.api.logging.logger
import me.devnatan.katan.api.security.account.Account
import me.devnatan.katan.api.server.Server
import me.devnatan.katan.api.server.ServerHolder
import me.devnatan.katan.common.EnvKeys.WS_DEPLOY_HTTPS_PORT
import me.devnatan.katan.common.EnvKeys.WS_DEPLOY_HTTP_HOST
import me.devnatan.katan.common.EnvKeys.WS_DEPLOY_HTTP_PORT
import me.devnatan.katan.common.EnvKeys.WS_DEPLOY_SSL_KEYALIAS
import me.devnatan.katan.common.EnvKeys.WS_DEPLOY_SSL_KEYSTORE_PASSWORD
import me.devnatan.katan.common.EnvKeys.WS_DEPLOY_SSL_PRIVATEKEY_PASSWORD
import me.devnatan.katan.common.EnvKeys.WS_DEPLOY_USE_SSL
import me.devnatan.katan.common.EnvKeys.WS_ENABLED
import me.devnatan.katan.common.util.*
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

            return if (config.getEnvBoolean("enabled", WS_ENABLED, true))
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
            host = deploy.getEnv("host", WS_DEPLOY_HTTP_HOST)!!
            port = deploy.getEnvInt("port", WS_DEPLOY_HTTP_PORT)!!
        }

        if (deploy.getEnvBoolean("ssl.enabled", WS_DEPLOY_USE_SSL, false)) {
            val ssl = deploy.getConfig("ssl")

            val alias = ssl.getEnv(
                "key-alias",
                WS_DEPLOY_SSL_KEYALIAS
            ) ?: error("Missing SSL key alias.")

            val keyStorePass = ssl.getEnv(
                "key-store-password",
                WS_DEPLOY_SSL_KEYSTORE_PASSWORD
            ) ?: error(
                "Missing SSL key store password."
            )
            
            val keyStore = buildKeyStore {
                certificate(alias) {
                    hash = HashAlgorithm.SHA256
                    sign = SignatureAlgorithm.ECDSA
                    password = keyStorePass
                }
            }

            sslConnector(keyStore, alias, {
                keyStorePass.toCharArray()
            }, {
                ssl.getEnv(
                    "private-key-password",
                    WS_DEPLOY_SSL_PRIVATEKEY_PASSWORD
                )?.toCharArray() ?: error(
                    "Missing SSL private key password."
                )
            }) {
                port = ssl.getEnvInt("port", WS_DEPLOY_HTTPS_PORT)!!
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