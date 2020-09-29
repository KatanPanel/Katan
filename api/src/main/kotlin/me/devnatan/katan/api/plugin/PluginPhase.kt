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