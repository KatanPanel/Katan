package me.devnatan.katan.api.plugin

import java.time.Instant

/**
 * Represents the current state of a [Plugin].
 * @property order state switch order.
 * @property timestamp when the state was set.
 * @property previous the state before that.
 */
sealed class PluginState(
    val order: Int,
    val timestamp: Instant,
    val previous: PluginState?
) : Comparable<PluginState> {

    /**
     * Represents the unloaded state of the plugin, represented as initial, of the first [order], but implicitly,
     * this state will only be the first if an [error] occurs during the loading of the plugin.
     */
    class Unloaded(timestamp: Instant) : PluginState(1, timestamp, null) {

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
    class Loaded(
        timestamp: Instant,
        previous: PluginState?
    ) : PluginState(2, timestamp, previous)

    /**
     * Represents the state of the plugin in which it is enabled and ready to be used, which can be disabled,
     * switching to [Disabled] and defining its error if an [Disabled.error] occurs during startup.
     */
    class Started(
        timestamp: Instant,
        previous: PluginState?
    ) : PluginState(3, timestamp, previous)

    /**
     * Represents the disabled state of the plugin, coming right after [Started].
     */
    class Disabled(
        timestamp: Instant,
        previous: PluginState?
    ) : PluginState(4, timestamp, previous) {

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

/**
 * Returns the [Instant] when the plugin was loaded.
 */
val PluginState.loadedAt: Instant
    get() {
        if (this is PluginState.Loaded)
            return timestamp

        check(previous != null)
        return previous.loadedAt
    }

/**
 * Returns the [Instant] when the plugin was started.
 */
val PluginState.startedAt: Instant
    get() {
        if (this is PluginState.Started)
            return timestamp

        check(previous != null)
        return previous.startedAt
    }

/**
 * Returns the [Instant] when the plugin was disabled.
 */
val PluginState.disabledAt: Instant
    get() {
        if (this is PluginState.Disabled)
            return timestamp

        check(previous != null)
        return previous.disabledAt
    }

/**
 * Returns the [Instant] when the plugin was unloaded.
 */
val PluginState.unloadedAt: Instant
    get() {
        if (this is PluginState.Unloaded)
            return timestamp

        check(previous != null)
        return previous.unloadedAt
    }