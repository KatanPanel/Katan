package me.devnatan.katan.api.plugin

import java.time.Instant

/**
 * Represents the current state of a plugin.
 * @property order state switch order.
 */
sealed class PluginState(val order: Int) : Comparable<PluginState> {

    /**
     * Represents the unloaded state of the plugin, represented as initial, of the first [order], but implicitly,
     * this state will only be the first if an [error] occurs during the loading of the plugin.
     */
    class Unloaded : PluginState(1) {

        /**
         * The error that occurred while loading the plugin if there
         * is an error that has not been handled by [PluginManager]
         * @see Loaded
         */
        var error: Throwable? = null

    }

    /**
     * Represents the loaded state of the plugin, which can be started at any time,
     * changing its state to [Started] or [Unloaded] in case of an error.
     */
    class Loaded(val loadedAt: Instant) : PluginState(2)

    /**
     * Represents the state of the plugin in which it is enabled and ready to be used, which can be disabled,
     * switching to [Disabled] and defining its error if an [Disabled.error] occurs during startup.
     */
    class Started(val startedAt: Instant) : PluginState(3)

    /**
     * Represents the disabled state of the plugin, coming right after [Started].
     */
    class Disabled(val disabledAt: Instant) : PluginState(4) {

        /**
         * The error that occurred while starting the plugin if there
         * is an error that has not been handled by [PluginManager]
         * @see Started
         */
        var error: Throwable? = null

    }

    override fun compareTo(other: PluginState): Int {
        return order.compareTo(other.order)
    }

}