package me.devnatan.katan.api.plugin

import me.devnatan.katan.api.Descriptor

/**
 * It represents the dependency of a plugin, described through its [descriptor],
 * which is the same as the plugin. Dependencies are loaded with the plugin's classloader
 * during the plugin's initialization, if there are non-[optional] dependencies that are not loaded for
 * the plugin during its initialization they will veto the continued loading of the plugin.
 */
open class PluginDependency(val descriptor: Descriptor) {

    var value: (() -> Any?)? = null

    /**
     * If this dependency is not required during the plugin's initialization.
     * Non-optional dependencies prevent the plugin from starting if they are not loaded.
     */
    var optional: Boolean = false
    val exclusions: MutableSet<String> = hashSetOf()

    /**
     * Excludes the dependency from this dependency, that is, it will not be loaded into
     * the plugin's class loader, this is good for avoiding version conflicts for example.
     * @param dependency dependency name
     */
    fun exclude(dependency: String) {
        exclusions.add(dependency)
    }

}