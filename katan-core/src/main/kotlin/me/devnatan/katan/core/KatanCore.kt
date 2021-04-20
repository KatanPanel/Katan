package me.devnatan.katan.core

import br.com.devsrsouza.eventkt.EventScope
import com.fasterxml.jackson.databind.ObjectMapper
import com.typesafe.config.Config
import kotlinx.coroutines.CoroutineName
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Job
import me.devnatan.katan.api.*
import me.devnatan.katan.api.annotations.UnstableKatanApi
import me.devnatan.katan.api.cache.Cache
import me.devnatan.katan.api.cache.UnavailableCacheProvider
import me.devnatan.katan.api.command.CommandManager
import me.devnatan.katan.api.logging.logger
import me.devnatan.katan.api.plugin.KatanInit
import me.devnatan.katan.api.plugin.KatanStarted
import me.devnatan.katan.api.security.crypto.Hash
import me.devnatan.katan.api.security.permission.PermissionKey
import me.devnatan.katan.api.service.get
import me.devnatan.katan.common.util.get
import me.devnatan.katan.core.cache.RedisCacheProvider
import me.devnatan.katan.core.crypto.BcryptHash
import me.devnatan.katan.core.database.DatabaseManager
import me.devnatan.katan.core.database.jdbc.JDBCConnector
import me.devnatan.katan.core.docker.DockerEventsListener
import me.devnatan.katan.core.docker.DockerManager
import me.devnatan.katan.core.impl.account.AccountManagerImpl
import me.devnatan.katan.core.impl.cli.CommandManagerImpl
import me.devnatan.katan.core.impl.game.GameManagerImpl
import me.devnatan.katan.core.impl.permission.PermissionManagerImpl
import me.devnatan.katan.core.impl.plugin.DefaultPluginManager
import me.devnatan.katan.core.impl.server.DockerServerManager
import me.devnatan.katan.core.impl.services.ServiceManagerImpl
import me.devnatan.katan.core.repository.JDBCAccountsRepository
import me.devnatan.katan.core.repository.JDBCServersRepository
import me.devnatan.katan.io.file.DefaultFileSystemAccessor
import me.devnatan.katan.io.file.DockerHostFileSystem
import me.devnatan.katan.io.file.PersistentFileSystem
import org.slf4j.Logger
import redis.clients.jedis.JedisPool
import redis.clients.jedis.JedisPoolConfig
import java.io.File
import java.util.*
import kotlin.system.exitProcess

@OptIn(UnstableKatanApi::class)
class KatanCore(
    val config: Config,
    override val environment: KatanEnvironment,
    override val translator: Translator,
    val rootDirectory: File
) : Katan, CoroutineScope by CoroutineScope(Job() + CoroutineName("Katan")) {

    companion object {

        const val DATABASE_DIALECT_FALLBACK = "H2"
        const val DEFAULT_VALUE = "default"
        val log: Logger = logger<Katan>()

    }

    override val platform: Platform = currentPlatform()
    override lateinit var accountManager: AccountManagerImpl
    override lateinit var serverManager: DockerServerManager
    override val pluginManager = DefaultPluginManager(this)
    override val serviceManager = ServiceManagerImpl(this)
    override val gameManager = GameManagerImpl(this)
    override lateinit var cache: Cache<Any>
    override val eventBus: EventScope = EventBus()
    lateinit var hash: Hash
    override val permissionManager = PermissionManagerImpl()
    private val dockerEventsListener = DockerEventsListener(this)
    override val commandManager: CommandManager = CommandManagerImpl()

    val docker = DockerManager(this)
    val objectMapper = ObjectMapper()
    val databaseManager = DatabaseManager(this)

    val fs = selectFileSystem()

    override val fileSystemAccessor = DefaultFileSystemAccessor(config, fs)

    init {
        coroutineContext[Job]!!.invokeOnCompletion {
            log.error("[FATAL ERROR]")
            log.error("Katan main worker has been canceled and this is not expected to happen.")
            log.error("This will cause unexpected problems in the application.")
            log.error("See the logs files to extract more information. Exiting process.")
            log.trace(null, it)
            exitProcess(1)
        }
    }

    private fun caching() {
        val redis = config.getConfig("redis")
        if (!redis.get("use", false)) {
            log.warn(translator.translate("katan.redis.disabled"))
            log.warn(
                translator.translate(
                    "katan.redis.alert",
                    "https://redis.io/"
                )
            )
            return
        }

        try {
            // we have to use the pool instead of the direct client due to Katan nature,
            // the default instance of Jedis (without pool) is not thread-safe
            cache = RedisCacheProvider(
                JedisPool(
                    JedisPoolConfig(),
                    redis.get("host", "localhost")
                )
            )
            log.info(translator.translate("katan.redis.ready"))
        } catch (e: Throwable) {
            cache = UnavailableCacheProvider()
            log.error(translator.translate("katan.redis.connection-failed"))
        }
    }

    suspend fun start() {
        log.info(
            translator.translate(
                "katan.starting",
                Katan.VERSION,
                translator.translate("katan.env.$environment")
                    .toLowerCase(translator.locale)
            )
        )
        log.info(translator.translate("katan.platform", "$platform"))

        val zoneId = config.get("timezone", "default")
        if (zoneId != "default") {
            val timezone = TimeZone.getTimeZone(zoneId)
            System.setProperty(Katan.TIMEZONE_PROPERTY, timezone.id)
            log.info(
                translator.translate(
                    "katan.timezone",
                    timezone.displayName
                )
            )
        }

        docker.initialize()
        databaseManager.connect()
        for (defaultKey in PermissionKey.defaultPermissionKeys)
            permissionManager.registerPermissionKey(defaultKey)

        gameManager.register()
        serverManager = DockerServerManager(
            this,
            JDBCServersRepository(databaseManager.database as JDBCConnector)
        )
        accountManager = AccountManagerImpl(
            this,
            JDBCAccountsRepository(databaseManager.database as JDBCConnector)
        )
        caching()
        pluginManager.loadPlugins()

        pluginManager.callHandlers(KatanInit)
        serverManager.loadServers()

        hash = when (val algorithm = config.getString("security.crypto.hash")) {
            DEFAULT_VALUE, BcryptHash.NAME -> BcryptHash()
            else -> serviceManager.get<Hash>().find {
                it.name == algorithm
            }
                ?: throw IllegalArgumentException("Unsupported hashing algorithm: $algorithm")
        }

        log.info(translator.translate("katan.selected-hash", hash.name))
        accountManager.loadAccounts()
        dockerEventsListener.listen()

        pluginManager.callHandlers(KatanStarted)
    }

    suspend fun close() {
        pluginManager.disableAll()
        fs.close()

        if (::cache.isInitialized)
            cache.close()
    }

    private fun selectFileSystem(): PersistentFileSystem {
        // only Docker Host is currently available
        return DockerHostFileSystem(this)
    }

}