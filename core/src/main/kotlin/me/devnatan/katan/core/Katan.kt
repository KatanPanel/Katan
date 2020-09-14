package me.devnatan.katan.core

import com.fasterxml.jackson.databind.ObjectMapper
import com.github.dockerjava.api.DockerClient
import com.github.dockerjava.core.DockerClientBuilder
import com.github.dockerjava.core.KeystoreSSLConfig
import com.github.dockerjava.core.LocalDirectorySSLConfig
import com.github.dockerjava.jaxrs.JerseyDockerHttpClient
import kotlinx.coroutines.CoroutineName
import kotlinx.coroutines.CoroutineScope
import me.devnatan.katan.core.manager.AccountManager
import me.devnatan.katan.core.manager.ServerManager
import me.devnatan.katan.core.manager.WebSocketManager
import me.devnatan.katan.core.sql.dao.AccountsTable
import me.devnatan.katan.core.sql.dao.ServerHoldersTable
import me.devnatan.katan.core.sql.dao.ServersTable
import org.jetbrains.exposed.sql.Database
import org.jetbrains.exposed.sql.SchemaUtils
import org.jetbrains.exposed.sql.transactions.transaction
import org.slf4j.LoggerFactory
import java.net.URI
import java.security.KeyStore
import java.security.Security
import kotlin.system.measureTimeMillis

class Katan(val config: KatanConfiguration, val objectMapper: ObjectMapper) :
    CoroutineScope by CoroutineScope(CoroutineName("Katan")) {

    private companion object {
        val logger = LoggerFactory.getLogger(Katan::class.java)!!
    }

    lateinit var database: Database
    lateinit var docker: DockerClient

    lateinit var accountManager: AccountManager
    lateinit var webSocketManager: WebSocketManager
    lateinit var serverManager: ServerManager

    fun start() {
        val mysql = config.get<Map<*, *>>("mysql")
        database = Database.connect(
            mysql["url"] as String,
            mysql["driver"] as String,
            mysql["user"] as String,
            mysql["password"] as String
        )

        logger.info("[Database] Connecting to {}...", mysql["url"])

        val took = measureTimeMillis {
            transaction(database) {
                try {
                    SchemaUtils.create(
                        AccountsTable,
                        ServersTable,
                        ServerHoldersTable
                    )
                } catch (e: Throwable) {
                    logger.error("Couldn't connect to database, please check your credentials and try again.")
                    end(e)
                }
            }
        }

        logger.info("[Database] Connected successfully, took {}ms.", String.format("%.2f", took.toFloat()))
        val jerseyClient = JerseyDockerHttpClient.Builder().dockerHost(URI(config.get<String>("docker.host")))

        if (config.getOrDefault("docker.ssl.enabled", false)) {
            logger.info("[SSL] Configuring...")
            jerseyClient.sslConfig(
                when (config.get<String>("docker.ssl.provider")) {
                    "CERT" -> {
                        val path: String = config["docker.ssl.cert.path"]
                        logger.info("[SSL] Certification path located at {}.", path)
                        LocalDirectorySSLConfig(path)
                    }
                    "KEY_STORE" -> {
                        val type = config.getOrNull("docker.ssl.key-store.type") ?: KeyStore.getDefaultType()
                        val keystore = KeyStore.getInstance(type)
                        logger.info(
                            "[SSL] Using {} key store type (${Security.getProviders().joinToString(", ")}).",
                            type
                        )

                        KeystoreSSLConfig(keystore, config["docker.ssl.key-store.password"])
                    }
                    else -> end(IllegalArgumentException("Unrecognized SSL provider. Must be: CERT or KEY_STORE"))
                }
            )
        }

        docker = DockerClientBuilder.getInstance().withDockerHttpClient(jerseyClient.build()).build()
        accountManager = AccountManager(this)
        serverManager = ServerManager(this)
        webSocketManager = WebSocketManager(this)
    }

    private fun end(e: Throwable): Nothing = throw RuntimeException(e)

}