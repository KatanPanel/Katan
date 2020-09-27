package me.devnatan.katan.api

import me.devnatan.katan.api.manager.AccountManager
import me.devnatan.katan.api.manager.ServerManager

/**
 * Interface that provides access to Katan handlers without having
 * direct access to it, useful for plugins and extensions.
 */
interface Katan {

    /**
     * Platform information that Katan is running on.
     */
    val platform: Platform

    /**
     * The [AccountManager] for this instance.
     */
    val accountManager: AccountManager

    /**
     * The [ServerManager] for this instance.
     */
    val serverManager: ServerManager

}