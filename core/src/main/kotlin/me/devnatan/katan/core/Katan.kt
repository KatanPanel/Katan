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
import kotlinx.coroutines.CoroutineName
import kotlinx.coroutines.CoroutineScope
import me.devnatan.katan.common.get
import me.devnatan.katan.common.getStringMap
import me.devnatan.katan.core.database.DatabaseConnector
import me.devnatan.katan.core.database.jdbc.*
import me.devnatan.katan.core.exceptions.throwSilent
import me.devnatan.katan.core.manager.AccountManager
import me.devnatan.katan.core.manager.DockerServerManager
import me.devnatan.katan.core.repository.JDBCServersRepository
import org.slf4j.LoggerFactory
import java.net.URI
import java.security.KeyStore

class Katan(val config: Config) :
    CoroutineScope by CoroutineScope(CoroutineName("Katan")) {

    companion object {

        val logger = LoggerFactory.getLogger(Katan::class.java)!!

        val objectMapper by lazy {
            jacksonObjectMapper().apply {
                enable(SerializationFeature.INDENT_OUTPUT, SerializationFeature.CLOSE_CLOSEABLE)
                disable(SerializationFeature.FAIL_ON_EMPTY_BEANS)
                setSerializationInclusion(JsonInclude.Include.NON_NULL)
                setDefaultPrettyPrinter(DefaultPrettyPrinter().apply {
                    indentArraysWith(DefaultPrettyPrinter.FixedSpaceIndenter.instance)
                    indentObjectsWith(DefaultIndenter("  ", "\n"))
                })
                propertyNamingStrategy = PropertyNamingStrategy.KEBAB_CASE
            }
        }

    }

    lateinit var database: DatabaseConnector<*>
    lateinit var docker: DockerClient

    lateinit var accountManager: AccountManager
    lateinit var serverManager: DockerServerManager

    private suspend fun database() {
        val db = config.getConfig("database")
        val dialect = db.get("source", "H2")
        logger.info("Using $dialect as database dialect.")

        val dialectName = dialect.toLowerCase()
        val settings = db.getConfig(dialectName)

        logger.info("Using $dialect as database dialect.")
        suspend fun <C : JDBCConnector<S>, S : JDBCSettings> connectWith(connector: C, settings: S): C {
            logger.info("Initializing connector ${connector::class.simpleName}.")
            return connector.also { it.connect(settings) }
        }

        database = when (dialectName) {
            "mysql" -> connectWith(MySQLConnector(), JDBCRemoteSettings(
                settings.get("host", "localhost:3306"),
                settings.get("user", "root"),
                settings.getString("password"),
                settings.get("database", "katan"),
                settings.getStringMap("properties")
            ))
            "h2" -> connectWith(H2Connector(settings.get("inMemory", true)), JDBCLocalSettings(
                settings.get("file", "./katan.db"),
                settings.getStringMap("properties")
            ))
            else -> throwSilent(IllegalArgumentException("Database dialect $dialect is not supported"))
        }
    }

    private fun docker() {
        logger.info("Configuring Docker...")
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
                        logger.info("Docker SSL certification path located at {}.", path)
                        LocalDirectorySSLConfig(path)
                    }
                    "KEY_STORE" -> {
                        val type = dockerConfig.get("keyStore.provider") ?: KeyStore.getDefaultType()
                        val keystore = KeyStore.getInstance(type)
                        logger.info(
                            "Using {} as Docker SSL key store type.",
                            type
                        )

                        KeystoreSSLConfig(keystore, dockerConfig.getString("keyStore.password"))
                    }
                    else -> throwSilent(IllegalArgumentException("Unrecognized Docker SSL provider. Must be: CERT or KEY_STORE"))
                }
            )
        }

        docker = DockerClientBuilder.getInstance().withDockerHttpClient(jerseyClient.build()).build()
    }

    suspend fun start() {
        database()
        docker()
        accountManager = AccountManager(this)
        serverManager = DockerServerManager(this, when (database) {
            is JDBCConnector -> JDBCServersRepository(this, database as JDBCConnector<*>)
            else -> throwSilent(IllegalArgumentException("No servers repository available for $database"))
        })
    }

}