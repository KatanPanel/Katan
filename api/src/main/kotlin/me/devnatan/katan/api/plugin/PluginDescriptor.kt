package me.devnatan.katan.api.plugin

import me.devnatan.katan.api.Version

/**
 * Represents the basic information of a plugin, as it should be described,
 * it is also used as a dependency filtering element for example through the [version] of the descriptor.
 * @property name descriptor name.
 * @property version descriptor version.
 * @property author descriptor author (only used for credits in the plugin).
 */
data class PluginDescriptor(val name: String, val version: Version? = null, val author: String? = null)