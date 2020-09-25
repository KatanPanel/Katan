package me.devnatan.katan.core

import com.github.dockerjava.api.DockerClient
import com.github.dockerjava.core.DockerClientBuilder
import com.github.dockerjava.core.KeystoreSSLConfig
import com.github.dockerjava.core.LocalDirectorySSLConfig
import com.github.dockerjava.jaxrs.JerseyDockerHttpClient
import com.typesafe.config.Config
import kotlinx.coroutines.CoroutineName
import kotlinx.coroutines.CoroutineScope
import me.devnatan.katan.api.Katan
import me.devnatan.katan.api.manager.AccountManager
import me.devnatan.katan.api.manager.ServerManager
import me.devnatan.katan.common.util.get
import me.devnatan.katan.core.database.DatabaseConnector
import me.devnatan.katan.core.database.SUPPORTED_CONNECTORS
import me.devnatan.katan.core.database.jdbc.JDBCConnector
import me.devnatan.katan.core.exceptions.silent
import me.devnatan.katan.core.manager.DefaultAccountManager
import me.devnatan.katan.core.manager.DockerServerManager
import me.devnatan.katan.core.repository.JDBCServersRepository
import org.slf4j.LoggerFactory
import java.net.URI
import java.security.KeyStore

class KatanCore(val config: Config) :
    CoroutineScope by CoroutineScope(CoroutineName("Katan")), Katan {

    companion object {

        const val DATABASE_DIALECT_FALLBACK = "H2"
        val logger = LoggerFactory.getLogger(KatanCore::class.java)!!

    }

    lateinit var database: DatabaseConnector
    lateinit var docker: DockerClient

    override lateinit var accountManager: AccountManager
    override lateinit var serverManager: ServerManager

    private suspend fun database() {
        val db = config.getConfig("database")
        val dialect = db.get("source", DATABASE_DIALECT_FALLBACK)
        logger.info("Using $dialect as database dialect (fallback to ${DATABASE_DIALECT_FALLBACK}).")

        val dialectSettings = runCatching {
            db.getConfig(dialect.toLowerCase())
        }.onFailure {
            throw IllegalArgumentException("Dialect properties not found: $dialect.").silent()
        }.getOrThrow()

        connectWith(dialect, dialectSettings, db.get("strict", true))
    }

    private suspend fun connectWith(dialect: String, config: Config, strict: Boolean) {
        val dialectName = dialect.toLowerCase()
        if (!SUPPORTED_CONNECTORS.containsKey(dialectName))
            throw IllegalArgumentException("Database dialect $dialect is not supported").silent()

        val (connector, settings) = SUPPORTED_CONNECTORS.getValue(dialectName).invoke(config)
        logger.info("Initializing connector ${connector::class.simpleName}.")

        runCatching {
            database = connector
            connector.connect(settings)
        }.onFailure {
            logger.error("Unable to connect to $dialect database.")
            if (strict || dialect.equals(DATABASE_DIALECT_FALLBACK, true)) {
                logger.error("{}", it.toString())
                throw it
            }

            logger.info("Strict mode is disabled, connecting again using fallback dialect $DATABASE_DIALECT_FALLBACK.")
            connectWith(DATABASE_DIALECT_FALLBACK, config, strict)
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
            jerseyClient.sslConfig(when (dockerConfig.getString("ssl.provider")) {
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
                else -> throw IllegalArgumentException("Unrecognized Docker SSL provider. Must be: CERT or KEY_STORE").silent()
            })
        }

        docker = DockerClientBuilder.getInstance().withDockerHttpClient(jerseyClient.build()).build()
    }

    suspend fun start() {
        database()
        docker()
        accountManager = DefaultAccountManager(this)
        serverManager = DockerServerManager(this, when (database) {
            is JDBCConnector -> JDBCServersRepository(this, database as JDBCConnector)
            else -> throw IllegalArgumentException("No servers repository available for $database").silent()
        })
    }

}