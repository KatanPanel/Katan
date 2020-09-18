package me.devnatan.katan.core

import com.fasterxml.jackson.annotation.JsonInclude
import com.fasterxml.jackson.core.util.DefaultIndenter
import com.fasterxml.jackson.core.util.DefaultPrettyPrinter
import com.fasterxml.jackson.databind.PropertyNamingStrategy
import com.fasterxml.jackson.databind.SerializationFeature
import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import com.github.dockerjava.api.DockerClient
import com.github.dockerjava.core.DockerClientBuilder
import com.github.dockerjava.core.KeystoreSSLConfig
import com.github.dockerjava.core.LocalDirectorySSLConfig
import com.github.dockerjava.jaxrs.JerseyDockerHttpClient
import com.typesafe.config.Config
import io.netty.handler.codec.http.QueryStringEncoder
import kotlinx.coroutines.CoroutineName
import kotlinx.coroutines.CoroutineScope
import me.devnatan.katan.core.dao.AccountsTable
import me.devnatan.katan.core.dao.ServerHoldersTable
import me.devnatan.katan.core.dao.ServersTable
import me.devnatan.katan.core.manager.AccountManager
import me.devnatan.katan.core.manager.ServerManager
import me.devnatan.katan.core.manager.WebSocketManager
import org.jetbrains.exposed.sql.Database
import org.jetbrains.exposed.sql.SchemaUtils
import org.jetbrains.exposed.sql.transactions.transaction
import org.slf4j.LoggerFactory
import java.net.URI
import java.security.KeyStore
import kotlin.system.measureTimeMillis

class Katan(val config: Config) :
    CoroutineScope by CoroutineScope(CoroutineName("Katan")) {

    companion object {
        val logger = LoggerFactory.getLogger(Katan::class.java)!!

        private val connectors = arrayOf(mapOf(
            "name" to "MySQL",
            "driver" to "com.mysql.cj.jdbc.Driver",
            "url" to "jdbc:mysql://%s/%s"
        ))

        val objectMapper by lazy {
            jacksonObjectMapper().apply {
                enable(SerializationFeature.INDENT_OUTPUT, SerializationFeature.CLOSE_CLOSEABLE)
                disable(SerializationFeature.FAIL_ON_EMPTY_BEANS)
                setSerializationInclusion(JsonInclude.Include.NON_NULL)
                setDefaultPrettyPrinter(DefaultPrettyPrinter().apply {
                    indentArraysWith(DefaultPrettyPrinter.FixedSpaceIndenter.instance)
                    indentObjectsWith(DefaultIndenter("  ", "\n"))
                })
                propertyNamingStrategy = PropertyNamingStrategy.SNAKE_CASE
            }
        }
    }

    lateinit var database: Database
    lateinit var docker: DockerClient

    lateinit var accountManager: AccountManager
    lateinit var webSocketManager: WebSocketManager
    lateinit var serverManager: ServerManager

    private fun database() {
        val mysql = config.getConfig("storage")
        val type = mysql.getString("type")

        val connector = connectors.firstOrNull {
            it.getValue("name") == type
        } ?: throw IllegalArgumentException("Unknown database connector $type")

        val host = String.format(connector.getValue("url"), mysql.getString("host"), mysql.getString("database"))
        val url = QueryStringEncoder(host).apply {
            for ((name, value) in mysql.getConfig("properties").entrySet()) {
                addParam(name, value.unwrapped().toString())
            }
        }.toString()
        database = Database.connect(
            url,
            connector.getValue("driver"),
            mysql.getString("user"),
            mysql.getString("password")
        )

        logger.info("[Database] Connecting to {}...", mysql.getString("host"))

        val time = measureTimeMillis {
            transaction(database) {
                try {
                    SchemaUtils.create(
                        AccountsTable,
                        ServersTable,
                        ServerHoldersTable
                    )
                } catch (e: Throwable) {
                    logger.error("[Database] Couldn't connect to database, please check your credentials and try again.")
                    logger.error("[Database] {}", e.toString())
                    throw e
                }
            }
        }

        logger.info("[Database] Connected successfully, took {}s.", String.format("%.2f", time / 1000.0f))
    }

    private fun docker() {
        val dockerConfig = config.getConfig("docker")
        val properties = dockerConfig.getConfig("properties")
        val jerseyClient = JerseyDockerHttpClient.Builder().dockerHost(URI(dockerConfig.getString("host")))
            .connectTimeout(properties.getInt("connectTimeout"))
            .readTimeout(properties.getInt("readTimeout"))

        if (dockerConfig.get("ssl.enabled", false)) {
            jerseyClient.sslConfig(
                when (dockerConfig.getString("ssl.provider")) {
                    "CERT" -> {
                        val path = dockerConfig.getString("ssl.certPath")
                        logger.info("[Docker] SSL certification path located at {}.", path)
                        LocalDirectorySSLConfig(path)
                    }
                    "KEY_STORE" -> {
                        val type = dockerConfig.get("keyStore.provider") ?: KeyStore.getDefaultType()
                        val keystore = KeyStore.getInstance(type)
                        logger.info(
                            "[Docker] Using {} as SSL key store type.",
                            type
                        )

                        KeystoreSSLConfig(keystore, dockerConfig.getString("keyStore.password"))
                    }
                    else -> throw IllegalArgumentException("Unrecognized SSL provider. Must be: CERT or KEY_STORE")
                }
            )
        }

        docker = DockerClientBuilder.getInstance().withDockerHttpClient(jerseyClient.build()).build()
    }

    fun start() {
        database()
        docker()
        accountManager = AccountManager(this)
        serverManager = ServerManager(this)
        webSocketManager = WebSocketManager(this)
    }

}