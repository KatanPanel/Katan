package me.devnatan.katan.api.plugin

/**
 * Represents a phase during the initialization period of a plugin
 * @property key a key for this phase
 */
class PluginPhase(val key: String) {

    override fun toString(): String {
        return "Phase($key)"
    }

}

/**
 * Phase called when the plugin is started.
 */
val PluginStarted = PluginPhase("PluginStarted")

/**
 * Phase called when the plugin is stopped.
 */
val PluginStopped = PluginPhase("PluginStopped")

/**
 * Phase called when the plugin is loaded.
 */
val PluginLoaded = PluginPhase("PluginLoaded")

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