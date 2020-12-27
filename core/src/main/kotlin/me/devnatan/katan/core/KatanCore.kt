package me.devnatan.katan.core

import br.com.devsrsouza.eventkt.EventScope
import com.fasterxml.jackson.databind.ObjectMapper
import com.github.dockerjava.api.DockerClient
import com.github.dockerjava.core.DefaultDockerClientConfig
import com.github.dockerjava.core.DockerClientImpl
import com.github.dockerjava.core.KeystoreSSLConfig
import com.github.dockerjava.core.LocalDirectorySSLConfig
import com.github.dockerjava.okhttp.OkDockerHttpClient
import com.typesafe.config.Config
import kotlinx.coroutines.CoroutineName
import kotlinx.coroutines.CoroutineScope
import me.devnatan.katan.api.*
import me.devnatan.katan.api.annotations.UnstableKatanApi
import me.devnatan.katan.api.cache.Cache
import me.devnatan.katan.api.cache.UnavailableCacheProvider
import me.devnatan.katan.api.game.GameManager
import me.devnatan.katan.api.plugin.KatanInit
import me.devnatan.katan.api.plugin.KatanStarted
import me.devnatan.katan.api.security.crypto.Hash
import me.devnatan.katan.api.security.permission.DefaultPermissionKeys
import me.devnatan.katan.api.service.get
import me.devnatan.katan.common.util.get
import me.devnatan.katan.core.cache.RedisCacheProvider
import me.devnatan.katan.core.crypto.BcryptHash
import me.devnatan.katan.core.database.DatabaseManager
import me.devnatan.katan.core.database.jdbc.JDBCConnector
import me.devnatan.katan.core.docker.DockerEventsListener
import me.devnatan.katan.core.impl.account.AccountsManagerImpl
import me.devnatan.katan.core.impl.game.GameManagerImpl
import me.devnatan.katan.core.impl.permission.PermissionManagerImpl
import me.devnatan.katan.core.impl.plugin.DefaultPluginManager
import me.devnatan.katan.core.impl.server.DockerServerManager
import me.devnatan.katan.core.impl.services.ServiceManagerImpl
import me.devnatan.katan.core.repository.JDBCAccountsRepository
import me.devnatan.katan.core.repository.JDBCServersRepository
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import redis.clients.jedis.JedisPool
import redis.clients.jedis.JedisPoolConfig
import java.security.KeyStore
import java.util.*
import kotlin.system.exitProcess

@OptIn(UnstableKatanApi::class)
class KatanCore(val config: Config, override val environment: KatanEnvironment, override val translator: Translator) :
    Katan, CoroutineScope by CoroutineScope(CoroutineName("Katan")) {

    companion object {

        const val DATABASE_DIALECT_FALLBACK = "H2"
        const val DEFAULT_VALUE = "default"
        val logger: Logger = LoggerFactory.getLogger(Katan::class.java)

    }

    val objectMapper = ObjectMapper()
    override val platform: Platform = currentPlatform()
    lateinit var docker: DockerClient
    override lateinit var accountManager: AccountsManagerImpl
    override lateinit var serverManager: DockerServerManager
    override val pluginManager = DefaultPluginManager(this)
    override val serviceManager = ServiceManagerImpl()
    override lateinit var gameManager: GameManager
    override lateinit var cache: Cache<Any>
    override val eventBus: EventScope = EventBus()
    lateinit var hash: Hash
    lateinit var databaseManager: DatabaseManager
    override val permissionManager = PermissionManagerImpl()
    private val dockerEventsListener = DockerEventsListener(this)

    init {
        val value = config.get("timezone", DEFAULT_VALUE)
        if (value != DEFAULT_VALUE) {
            val timezone = TimeZone.getTimeZone(value)
            System.setProperty("katan.timezone", timezone.id)
            logger.info(translator.translate("katan.timezone", timezone.displayName))
        }
    }

    private fun docker() {
        logger.info(translator.translate("katan.docker.config"))
        val dockerLogger = LoggerFactory.getLogger("Docker")
        val dockerConfig = config.getConfig("docker")
        val host = System.getenv("KATAN_DOCKER_URI") ?: dockerConfig.getString("host")
        if (host.startsWith("unix") && platform.isWindows()) {
            logger.error(translator.translate("katan.docker.unix-domain-sockets", host))
            exitProcess(0)
        }

        val tls = dockerConfig.get("tls.verify", false)
        val clientConfig = DefaultDockerClientConfig.createDefaultConfigBuilder()
            .withDockerHost(host)
            .withDockerTlsVerify(tls)

        if (tls) {
            dockerLogger.info(translator.translate("katan.docker.tls-enabled"))
            clientConfig.withDockerCertPath(dockerConfig.getString("tls.certPath"))
        } else
            dockerLogger.warn(
                translator.translate("katan.docker.tls-disabled", "https://docs.docker.com/engine/security/https/")
            )

        if (dockerConfig.get("ssl.enabled", false)) {
            clientConfig.withCustomSslConfig(
                when (dockerConfig.getString("ssl.provider")) {
                    "CERT" -> {
                        val path = dockerConfig.getString("ssl.certPath")
                        dockerLogger.info(translator.translate("katan.docker.cert-loaded", path))
                        LocalDirectorySSLConfig(path)
                    }
                    "KEY_STORE" -> {
                        val type = dockerConfig.get("keyStore.provider") ?: KeyStore.getDefaultType()
                        val keystore = KeyStore.getInstance(type)
                        dockerLogger.info(translator.translate("katan.docker.ks-loaded", type))

                        KeystoreSSLConfig(keystore, dockerConfig.getString("keyStore.password"))
                    }
                    else -> throw IllegalArgumentException("Unrecognized Docker SSL provider. Must be: CERT or KEY_STORE")
                }
            )
        }

        val properties = dockerConfig.getConfig("properties")
        val httpConfig = clientConfig.build()
        docker = DockerClientImpl.getInstance(
            httpConfig, OkDockerHttpClient.Builder()
                .dockerHost(httpConfig.dockerHost)
                .sslConfig(httpConfig.sslConfig)
                .connectTimeout(properties.get("connectTimeout", 5000))
                .readTimeout(properties.get("readTimeout", 5000))
                .build()
        )

        // sends a ping to see if the connection will be established.
        docker.pingCmd().exec()
        dockerLogger.info(translator.translate("katan.docker.ready"))
    }

    private fun caching() {
        val redis = config.getConfig("redis")
        if (!redis.get("use", false)) {
            logger.warn(translator.translate("katan.redis.disabled"))
            logger.warn(translator.translate("katan.redis.alert", "https://redis.io/"))
            return
        }

        try {
            // we have to use the pool instead of the direct client due to Katan nature,
            // the default instance of Jedis (without pool) is not thread-safe
            cache = RedisCacheProvider(JedisPool(JedisPoolConfig(), redis.get("host", "localhost")))
            logger.info(translator.translate("katan.redis.ready"))
        } catch (e: Throwable) {
            cache = UnavailableCacheProvider()
            logger.error(translator.translate("katan.redis.connection-failed"))
        }
    }

    suspend fun start() {
        logger.info(
            translator.translate(
                "katan.starting",
                Katan.VERSION,
                translator.translate("katan.env.$environment").toLowerCase(translator.locale)
            )
        )
        logger.info(translator.translate("katan.platform", "$platform"))
        docker()
        databaseManager = DatabaseManager(this)
        databaseManager.connect()
        pluginManager.loadPlugins()
        serverManager = DockerServerManager(this, JDBCServersRepository(databaseManager.database as JDBCConnector))
        accountManager = AccountsManagerImpl(this, JDBCAccountsRepository(databaseManager.database as JDBCConnector))
        caching()

        for (defaultPermission in DefaultPermissionKeys.DEFAULTS)
            permissionManager.registerPermissionKey(defaultPermission)

        gameManager = GameManagerImpl(this)
        pluginManager.callHandlers(KatanInit)
        serverManager.loadServers()
        dockerEventsListener.listen()

        hash = when (val algorithm = config.getString("security.crypto.hash")) {
            DEFAULT_VALUE, BcryptHash.NAME -> BcryptHash()
            else -> serviceManager.get() ?: throw IllegalArgumentException("Unsupported hashing algorithm: $algorithm")
        }
        logger.info(translator.translate("katan.selected-hash", hash.name))
        accountManager.loadAccounts()

        pluginManager.callHandlers(KatanStarted)
    }

    suspend fun close() {
        pluginManager.disableAll()
        dockerEventsListener.close()
    }

}