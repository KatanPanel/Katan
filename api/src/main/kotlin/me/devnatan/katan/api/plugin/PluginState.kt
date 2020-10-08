package me.devnatan.katan.api.plugin

import java.time.Instant

sealed class PluginState(val parent: PluginState?, val error: Throwable?) : Comparable<PluginState> {

    abstract val order: Int

    class Loaded(val loadedAt: Instant, parent: PluginState, error: Throwable? = null) : PluginState(parent, error) {
        override val order: Int get() = 1
    }

    class Started(val startedAt: Instant, parent: PluginState, error: Throwable? = null) : PluginState(parent, error) {
        override val order: Int get() = 3
    }

    class Disabled(val disabledAt: Instant, parent: PluginState, error: Throwable? = null) :
        PluginState(parent, error) {
        override val order: Int get() = 3
    }

    class Unloaded(error: Throwable? = null) : PluginState(null, error) {
        override val order: Int get() = 4
    }

    override fun compareTo(other: PluginState): Int {
        return order.compareTo(other.order)
    }

}