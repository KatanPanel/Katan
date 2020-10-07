package me.devnatan.katan.api.plugin

import java.time.Instant

sealed class PluginState(val parent: PluginState?, val error: Throwable?) {

    class Loaded(val loadedAt: Instant, parent: Unloaded, error: Throwable? = null) : PluginState(parent, error)

    class Started(val startedAt: Instant, parent: Loaded, error: Throwable? = null) : PluginState(parent, error)

    class Stopped(val disabledAt: Instant, parent: Started, error: Throwable? = null) : PluginState(parent, error)

    class Unloaded(error: Throwable? = null) : PluginState(null, error)

}