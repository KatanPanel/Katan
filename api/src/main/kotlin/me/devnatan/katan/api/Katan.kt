package me.devnatan.katan.api

import me.devnatan.katan.api.cache.Cache
import me.devnatan.katan.api.manager.AccountManager
import me.devnatan.katan.api.manager.ServerManager

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

    /**
     * The caching provider for that instance.
     * Should NEVER return an uninitialized value, for this use [me.devnatan.katan.api.cache.UnavailableCacheProvider]
     */
    val cache: Cache<Any>

    /**
     * Returns the environment mode that has been defined for this instance.
     */
    val environment: KatanEnvironment

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

}