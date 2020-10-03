package me.devnatan.katan.core

import br.com.devsrsouza.eventkt.EventScope
import br.com.devsrsouza.eventkt.scopes.LocalEventScope
import br.com.devsrsouza.eventkt.scopes.SimpleEventScope
import com.github.dockerjava.api.DockerClient
import com.github.dockerjava.core.DefaultDockerClientConfig
import com.github.dockerjava.core.DockerClientImpl
import com.github.dockerjava.core.KeystoreSSLConfig
import com.github.dockerjava.core.LocalDirectorySSLConfig
import com.github.dockerjava.okhttp.OkDockerHttpClient
import com.typesafe.config.Config
import kotlinx.coroutines.CoroutineName
import kotlinx.coroutines.CoroutineScope
import me.devnatan.katan.api.Katan
import me.devnatan.katan.api.KatanEnvironment
import me.devnatan.katan.api.Platform
import me.devnatan.katan.api.cache.Cache
import me.devnatan.katan.api.cache.UnavailableCacheProvider
import me.devnatan.katan.api.currentPlatform
import me.devnatan.katan.api.manager.AccountManager
import me.devnatan.katan.api.manager.PluginManager
import me.devnatan.katan.common.KATAN_VERSION
import me.devnatan.katan.common.util.get
import me.devnatan.katan.core.cache.RedisCacheProvider
import me.devnatan.katan.core.crypto.Hash
import me.devnatan.katan.core.database.DatabaseConnector
import me.devnatan.katan.core.database.SUPPORTED_CONNECTORS
import me.devnatan.katan.core.database.jdbc.JDBCConnector
import me.devnatan.katan.core.exceptions.throwSilent
import me.devnatan.katan.core.manager.DefaultAccountManager
import me.devnatan.katan.core.manager.DefaultPluginManager
import me.devnatan.katan.core.manager.DockerServerManager
import me.devnatan.katan.core.repository.JDBCAccountsRepository
import me.devnatan.katan.core.repository.JDBCServersRepository
import org.slf4j.LoggerFactory
import redis.clients.jedis.JedisPool
import redis.clients.jedis.JedisPoolConfig
import java.io.UncheckedIOException
import java.net.ConnectException
import java.security.KeyStore
import kotlin.system.measureTimeMillis

class KatanCore(
    val config: Config,
    override val environment: KatanEnvironment,
    val locale: KatanLocale
) :
    CoroutineScope by CoroutineScope(CoroutineName("Katan")), Katan {

    companion object {

        const val DATABASE_DIALECT_FALLBACK = "H2"
        val logger = LoggerFactory.getLogger(Katan::class.java)!!

    }

    override val platform: Platform by lazy {
        currentPlatform()
    }

    lateinit var database: DatabaseConnector
    lateinit var docker: DockerClient

    override lateinit var accountManager: AccountManager
    override lateinit var serverManager: DockerServerManager
    override lateinit var pluginManager: PluginManager
    override lateinit var cache: Cache<Any>
    override lateinit var eventBus: EventScope
    lateinit var hash: Hash

    private suspend fun database() {
        val db = config.getConfig("database")
        val dialect = db.get("source", DATABASE_DIALECT_FALLBACK)
        logger.info(locale.internal("katan.database.dialect", dialect, DATABASE_DIALECT_FALLBACK))

        val dialectSettings = runCatching {
            db.getConfig(dialect.toLowerCase())
        }.onFailure {
            throwSilent(IllegalArgumentException("Dialect properties not found: $dialect."), logger)
        }.getOrThrow()

        connectWith(dialect, dialectSettings, db.get("strict", false))
    }

    private suspend fun connectWith(dialect: String, config: Config, strict: Boolean) {
        val dialectName = dialect.toLowerCase()
        if (!SUPPORTED_CONNECTORS.containsKey(dialectName))
            throwSilent(IllegalArgumentException("Database dialect $dialect is not supported"), logger)

        val (connector, settings) = SUPPORTED_CONNECTORS.getValue(dialectName).invoke(config)
        logger.info(locale.internal("katan.database.connector", connector::class.simpleName!!))

        runCatching {
            database = connector
            logger.info(locale.internal("katan.database.connecting", settings.toString()))
            val took = measureTimeMillis {
                connector.connect(settings)
            }
            logger.info(locale.internal("katan.database.connected", String.format("%.2f", took / 1000.0f)))
        }.onFailure {
            logger.error(locale.internal("katan.database.fail", dialect))
            if (strict || dialect.equals(DATABASE_DIALECT_FALLBACK, true))
                throwSilent(it, logger)

            logger.info(locale.internal("katan.database.strict", DATABASE_DIALECT_FALLBACK))
            connectWith(DATABASE_DIALECT_FALLBACK, config, strict)
        }
    }

    private fun docker() {
        logger.info(locale.internal("katan.docker.config"))
        val dockerLogger = LoggerFactory.getLogger(DockerClient::class.java)
        val dockerConfig = config.getConfig("docker")
        val tls = dockerConfig.get("tls.verify", false)
        val clientConfig = DefaultDockerClientConfig.createDefaultConfigBuilder()
            .withDockerHost(dockerConfig.getString("host"))
            .withDockerTlsVerify(tls)

        if (tls) {
            dockerLogger.info(locale.internal("katan.docker.tls-enabled"))
            clientConfig.withDockerCertPath(dockerConfig.getString("tls.certPath"))
        } else
            dockerLogger.warn(
                locale.internal(
                    "katan.docker.tls-disabled",
                    "https://docs.docker.com/engine/security/https/"
                )
            )

        if (dockerConfig.get("ssl.enabled", false)) {
            clientConfig.withCustomSslConfig(
                when (dockerConfig.getString("ssl.provider")) {
                    "CERT" -> {
                        val path = dockerConfig.getString("ssl.certPath")
                        dockerLogger.info(locale.internal("katan.docker.cert-loaded", path))
                        LocalDirectorySSLConfig(path)
                    }
                    "KEY_STORE" -> {
                        val type = dockerConfig.get("keyStore.provider") ?: KeyStore.getDefaultType()
                        val keystore = KeyStore.getInstance(type)
                        dockerLogger.info(locale.internal("katan.docker.ks-loaded", type))

                        KeystoreSSLConfig(keystore, dockerConfig.getString("keyStore.password"))
                    }
                    else -> throwSilent(
                        IllegalArgumentException("Unrecognized Docker SSL provider. Must be: CERT or KEY_STORE"),
                        logger
                    )
                }
            )
        }

        val properties = dockerConfig.getConfig("properties")
        val httpConfig = clientConfig.build()

        docker = runCatching {
            DockerClientImpl.getInstance(
                httpConfig, OkDockerHttpClient.Builder()
                    .dockerHost(httpConfig.dockerHost)
                    .sslConfig(httpConfig.sslConfig)
                    .connectTimeout(properties.get("connectTimeout", 5000))
                    .readTimeout(properties.get("readTimeout", 5000))
                    .build()
            )
        }.onFailure {
            throwSilent(it, dockerLogger)
        }.getOrThrow()

        // sends a ping to see if the connection will be established.
        try {
            docker.pingCmd().exec()
            dockerLogger.info(locale.internal("katan.docker.ready"))
        } catch (e: ConnectException) {
            throwSilent(e, dockerLogger)
        } catch (e: UncheckedIOException) {
            throwSilent(e.cause!!, dockerLogger)
        }
    }

    private fun caching() {
        val redis = config.getConfig("redis")
        if (!redis.get("use", false)) {
            logger.warn(locale.internal("katan.redis.disabled"))
            logger.warn(locale.internal("katan.redis.alert", "https://redis.io/"))
            return
        }

        val host = redis.get("host", "localhost")
        logger.info(locale.internal("katan.redis.host-info", host))

        try {
            // we have to use the pool instead of the direct client due to Katan nature,
            // the default instance of Jedis (without pool) is not thread-safe
            cache = RedisCacheProvider(JedisPool(JedisPoolConfig(), host))
            logger.info(locale.internal("katan.redis.ready"))
        } catch (e: Throwable) {
            cache = UnavailableCacheProvider()
            logger.error(locale.internal("katan.redis.connection-failed"))
            logger.error(e.message)
        }
    }

    suspend fun start() {
        logger.info(
            locale.internal(
                "katan.starting",
                KATAN_VERSION,
                locale.internal("katan.env.$environment").toLowerCase(locale.locale)
            )
        )
        logger.info(locale.internal("katan.platform", "${platform.os.name} ${platform.os.version}"))

        database()
        docker()
        caching()

        fun throwUnavailableRepository(
            type: String
        ): Nothing = throwSilent(
            IllegalArgumentException("No $type repository available for ${database::class.simpleName}"),
            logger
        )

        eventBus = SimpleEventScope(LocalEventScope())
        pluginManager = DefaultPluginManager()
        accountManager = DefaultAccountManager(
            this, when (database) {
                is JDBCConnector -> JDBCAccountsRepository(this, database as JDBCConnector)
                else -> throwUnavailableRepository("accounts")
            }
        )
        serverManager = DockerServerManager(
            this, when (database) {
                is JDBCConnector -> JDBCServersRepository(this, database as JDBCConnector)
                else -> throwUnavailableRepository("servers")
            }
        )
    }

    override suspend fun close() {

    }

}