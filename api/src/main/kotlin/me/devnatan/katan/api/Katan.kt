package me.devnatan.katan.api

import br.com.devsrsouza.eventkt.EventScope
import me.devnatan.katan.api.cache.Cache
import me.devnatan.katan.api.manager.AccountManager
import me.devnatan.katan.api.manager.PluginManager
import me.devnatan.katan.api.manager.ServerManager

/**
 * Interface that provides access to Katan handlers without having
 * direct access to it, useful for plugins and extensions.
 */
interface Katan {

    companion object {

        val VERSION = Version(0, 0, 1, "alpha")

    }

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