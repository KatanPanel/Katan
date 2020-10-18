package me.devnatan.katan.api

import br.com.devsrsouza.eventkt.EventScope
import me.devnatan.katan.api.annotations.UnstableKatanApi
import me.devnatan.katan.api.cache.Cache
import me.devnatan.katan.api.plugin.Plugin
import me.devnatan.katan.api.plugin.PluginManager
import me.devnatan.katan.api.security.account.AccountManager
import me.devnatan.katan.api.server.Server
import me.devnatan.katan.api.server.ServerManager
import me.devnatan.katan.api.services.ServicesManager

/**
 * Interface that provides access to Katan handlers without having
 * direct access to it, useful for plugins and extensions.
 */
interface Katan {

    companion object {

        /**
         * Returns the current version of the Katan.
         */
        val VERSION = Version(0, 0, 1)

    }

    /**
     * Platform on which this instance is currently running.
     */
    val platform: Platform

    /**
     * Returns the environment mode that has been defined for this instance.
     */
    val environment: KatanEnvironment

    /**
     * Query, creation and management of accounts
     */
    val accountManager: AccountManager

    /**
     * Returns the Katan services manager.
     */
    @UnstableKatanApi
    val servicesManager: ServicesManager

    /**
     * The caching provider for that instance.
     * Can return an uninitialized value, use [Cache.isAvailable] to check.
     */
    val cache: Cache<Any>

    /**
     * Returns the Katan [Server] manager.
     */
    val serverManager: ServerManager

    /**
     * Returns the Katan [Plugin] manager.
     */
    val pluginManager: PluginManager

    /**
     * Returns the event publisher for this entire instance.
     */
    val eventBus: EventScope

    /**
     * Terminate the current instance by interrupting pending tasks and stopping running services,
     * it must suspend until the end if there is no termination of the JVM process.
     */
    suspend fun close()

}