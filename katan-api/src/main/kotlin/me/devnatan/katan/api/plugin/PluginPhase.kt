package me.devnatan.katan.api.plugin

/**
 * Represents a phase during the initialization period of a plugin.
 * @property key a key for this phase
 */
inline class PluginPhase(val key: String) {

    override fun toString(): String {
        return "Phase($key)"
    }

}

/**
 * Represents a handler for the [PluginPhase], unlike events only
 * occur within the scope of the plugin, that is, it is the plugin for itself.
 */
interface PluginHandler {

    /**
     * Called when a [PluginPhase] occurs.
     */
    suspend fun handle()

}

/**
 * Inline implementation for [PluginHandler].
 */
internal class PluginHandlerImpl(inline val handler: suspend () -> Unit) : PluginHandler {

    override suspend fun handle() {
        handler()
    }

}

/**
 * Phase called when the plugin is loaded.
 */
val PluginLoaded = PluginPhase("PluginLoaded")

/**
 * Phase called when the plugin is started.
 */
val PluginEnabled = PluginPhase("PluginEnabled")

/**
 * Phase called when the plugin is stopped.
 */
val PluginDisabled = PluginPhase("PluginDisabled")

/**
 * Phase called when Katan starts the boot process.
 */
val KatanInit = PluginPhase("KatanInit")

/**
 * Phase called when the Katan is completely started.
 */
val KatanStarted = PluginPhase("KatanStarted")