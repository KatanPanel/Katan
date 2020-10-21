package me.devnatan.katan.api.plugin

import me.devnatan.katan.api.Descriptor
import me.devnatan.katan.api.Version

/**
 * Represents the basic information of a plugin, as it should be described,
 * it is also used as a dependency filtering element for example through the [version] of the descriptor.
 * @property name plugin name.
 * @property version plugin version.
 * @property author plugin author (only used for credits in the plugin).
 */
data class PluginDescriptor(
    override val name: String,
    val version: Version? = null,
    val author: String? = null
) : Descriptor {

    override fun toString(): String {
        return buildString {
            append(name)
            version?.let {
                append(" v$version")
            }
        }
    }

}