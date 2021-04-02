package me.devnatan.katan.api.plugin

import me.devnatan.katan.api.Descriptor
import me.devnatan.katan.api.Version

/**
 * Represents the basic information of a plugin, as it should be described,
 * it is also used as a dependency filtering element for example through the [version] of the descriptor.
 * @property id plugin name.
 * @property version plugin version.
 * @property author plugin author (only used for credits in the plugin).
 */
data class PluginDescriptor(
    override val id: String,
    val version: Version? = null,
    val author: String? = null
) : Descriptor {

    // plugins are not trusted providers
    override fun isTrusted(): Boolean = false

    override fun toString(): String {
        return buildString {
            append(id)
            version?.let {
                append(" v$version")
            }
        }
    }

    override fun equals(other: Any?): Boolean {
        return other is Descriptor && other.id == id
    }

}