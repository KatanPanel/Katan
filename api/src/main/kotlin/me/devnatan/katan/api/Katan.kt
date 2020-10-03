package me.devnatan.katan.api

import br.com.devsrsouza.eventkt.EventScope
import me.devnatan.katan.api.cache.Cache
import me.devnatan.katan.api.manager.AccountManager
import me.devnatan.katan.api.manager.PluginManager
import me.devnatan.katan.api.manager.ServerManager
import me.devnatan.katan.api.plugin.PluginPhase
import org.slf4j.event.Level

/**
 * Interface that provides access to Katan handlers without having
 * direct access to it, useful for plugins and extensions.
 */
interface Katan {

    /**
     * Platform on which this instance is currently running.
     */
    val platform: Platform

    /**
     * Query, creation and management of accounts
     */
    val accountManager: AccountManager

    /**
     * Responsible for the generation, handling and handling of absolutely
     * everything related to servers, from creation to query.
     */
    val serverManager: ServerManager

    val pluginManager: PluginManager

    /**
     * The caching provider for that instance.
     * Should NEVER return an uninitialized value, for this use [me.devnatan.katan.api.cache.UnavailableCacheProvider]
     */
    val cache: Cache<Any>

    /**
     * Returns the environment mode that has been defined for this instance.
     */
    val environment: KatanEnvironment

    val eventBus: EventScope

    /**
     * Terminate the current instance by interrupting pending tasks and stopping running services,
     * it must suspend until the end if there is no termination of the JVM process.
     */
    suspend fun close()

}

inline class KatanEnvironment(private val value: String) {

    companion object {

        const val LOCAL = "local"
        const val DEVELOPMENT = "dev"
        const val TESTING = "test"
        const val STAGING = "stage"
        const val PRODUCTION = "prod"

        /**
         * Returns all available development modes
         */
        val ALL: Array<String> by lazy {
            arrayOf(LOCAL, DEVELOPMENT, TESTING, STAGING, PRODUCTION)
        }

    }

    /**
     * Returns `true` if the current environment mode is in [DEVELOPMENT].
     */
    fun isLocal(): Boolean {
        return value == LOCAL
    }

    /**
     * Returns `true` if the current environment mode is in [DEVELOPMENT] or [LOCAL].
     */
    fun isDevelopment(): Boolean {
        return value == LOCAL || value == DEVELOPMENT
    }

    /**
     * Returns `true` if the current environment mode is in [TESTING] or [STAGING].
     */
    fun isTesting(): Boolean {
        return value == TESTING || value == STAGING
    }

    /**
     * Returns `true` if the current environment mode is in [PRODUCTION].
     */
    fun isProduction(): Boolean {
        return value == PRODUCTION
    }

    override fun toString(): String {
        return value
    }

}

/**
 * Returns the default recommended logging level for this environment mode.
 */
fun KatanEnvironment.defaultLogLevel(): Level = when {
    isLocal() -> Level.TRACE
    isDevelopment() || isTesting() -> Level.DEBUG
    else -> Level.INFO
}

/**
 * Phase called during the Katan setup process.
 */
val KatanConfiguration = PluginPhase("KatanConfiguration")

/**
 * Phase called when Katan starts the boot process.
 *
 */
val KatanInit = PluginPhase("KatanInit")

/**
 * Phase called when the Katan is completely started.
 */
val KatanStarted = PluginPhase("KatanStarted")

@Retention(AnnotationRetention.RUNTIME)
@Target(AnnotationTarget.CLASS, AnnotationTarget.FUNCTION)
@RequiresOptIn(
    "This is an internal Katan API that should not be used from outside of Katan Core.",
    RequiresOptIn.Level.ERROR
)
annotation class InternalKatanAPI
