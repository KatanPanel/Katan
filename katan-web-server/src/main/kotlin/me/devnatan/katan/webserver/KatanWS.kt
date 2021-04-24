package me.devnatan.katan.webserver

import com.fasterxml.jackson.annotation.JsonInclude
import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.databind.PropertyNamingStrategy
import com.fasterxml.jackson.databind.SerializationFeature
import com.fasterxml.jackson.databind.module.SimpleModule
import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import com.typesafe.config.Config
import com.typesafe.config.ConfigFactory
import io.ktor.network.tls.certificates.*
import io.ktor.network.tls.extensions.*
import io.ktor.server.engine.*
import io.ktor.server.netty.*
import io.ktor.util.*
import me.devnatan.katan.api.logging.logger
import me.devnatan.katan.api.security.account.Account
import me.devnatan.katan.api.server.Server
import me.devnatan.katan.api.server.ServerHolder
import me.devnatan.katan.common.EnvKeys.WS_DEPLOY_HOST
import me.devnatan.katan.common.EnvKeys.WS_DEPLOY_PORT
import me.devnatan.katan.common.EnvKeys.WS_DEPLOY_SSL_KEYALIAS
import me.devnatan.katan.common.EnvKeys.WS_DEPLOY_SSL_KEYSTORE_PASSWORD
import me.devnatan.katan.common.EnvKeys.WS_DEPLOY_SSL_PORT
import me.devnatan.katan.common.EnvKeys.WS_DEPLOY_SSL_PRIVATEKEY_PASSWORD
import me.devnatan.katan.common.EnvKeys.WS_DEPLOY_USE_SSL
import me.devnatan.katan.common.EnvKeys.WS_ENABLED
import me.devnatan.katan.common.util.*
import me.devnatan.katan.core.KatanCore
import me.devnatan.katan.webserver.auth.TokenManager
import me.devnatan.katan.webserver.serializers.AccountSerializer
import me.devnatan.katan.webserver.serializers.InstantSerializer
import me.devnatan.katan.webserver.serializers.ServerHolderSerializer
import me.devnatan.katan.webserver.serializers.ServerSerializer
import me.devnatan.katan.webserver.websocket.WebSocketManager
import org.slf4j.Logger
import java.time.Instant
import kotlin.text.toCharArray

class KatanWS(val katan: KatanCore, val config: Config) {

    companion object {
        val log: Logger = logger<KatanWS>()

        val objectMapper: ObjectMapper by lazy {
            jacksonObjectMapper().apply {
                propertyNamingStrategy = PropertyNamingStrategy.KEBAB_CASE
                deactivateDefaultTyping()
                enable(SerializationFeature.CLOSE_CLOSEABLE)
                disable(SerializationFeature.FAIL_ON_EMPTY_BEANS)
                setSerializationInclusion(JsonInclude.Include.NON_NULL)

                registerModule(SimpleModule("KatanWS").apply {
                    addSerializer(Account::class.java, AccountSerializer())
                    addSerializer(Instant::class.java, InstantSerializer())
                    addSerializer(
                        Server::class.java,
                        ServerSerializer()
                    )
                    addSerializer(
                        ServerHolder::class.java,
                        ServerHolderSerializer()
                    )
                })
            }
        }
    }

    object Initializer {

        @JvmStatic
        fun create(katan: KatanCore): KatanWS? {
            val config =
                ConfigFactory.load(ConfigFactory.parseFile(exportResource("webserver.conf", katan.rootDirectory)))!!

            return if (config.getEnvBoolean("enabled", WS_ENABLED, true))
                KatanWS(katan, config)
            else
                null
        }

    }

    val tokenManager: TokenManager = TokenManager(this)
    val webSocketManager = WebSocketManager(katan)
    private val server: ApplicationEngine = embeddedServer(Netty, setupEngine())

    private fun setupEngine() = applicationEngineEnvironment {
        module {
            installFeatures(this@KatanWS)
            router(this@KatanWS)
        }

        val deploy = this@KatanWS.config.getConfig("deployment")
        connector {
            host = deploy.getEnv("host", WS_DEPLOY_HOST)!!
            port = deploy.getEnvInt("port", WS_DEPLOY_PORT)!!
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

            @OptIn(KtorExperimentalAPI::class)
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
                port = ssl.getEnvInt("port", WS_DEPLOY_SSL_PORT)!!
            }
        }
    }

    @OptIn(KtorExperimentalAPI::class)
    fun start() {
        webSocketManager.listen()

        server.addShutdownHook {
            val shutdown = config.getConfig("deployment.shutdown")
            server.stop(
                shutdown.get("grace-period", 1000),
                shutdown.get("timeout", 5000),
            )
        }

        server.start(wait = false)
    }

    suspend fun close() {
        webSocketManager.close()
    }

}