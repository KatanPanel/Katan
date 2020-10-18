package me.devnatan.katan.api.plugin

import me.devnatan.katan.api.Version
import me.devnatan.katan.api.annotations.UnstableKatanApi

/**
 * Plugin dependency manager, responsible for loading, adding and removing plugin dependencies.
 * This can also be intertwined with another dependency management system.
 */
@UnstableKatanApi
interface PluginDependencyManager {

    /**
     * Adds the dependency from a descriptor.
     * @param descriptor the dependency descriptor.
     */
    fun addDependency(descriptor: PluginDescriptor): PluginDependency

    /**
     * Removes a dependency that has been previously added.
     * @param descriptor the dependency descriptor.
     */
    fun removeDependency(descriptor: PluginDescriptor): PluginDependency

}

/**
 * Adds a plugin that matches the specified [descriptor] by adding
 * it to the plugin's classpath and setting it as the plugin's pre-boot priority.
 * @param descriptor the dependency descriptor.
 */
@UnstableKatanApi
inline fun PluginDependencyManager.dependsOn(
    descriptor: PluginDescriptor,
    crossinline block: PluginDependency.() -> Unit = {}
): PluginDependency {
    return addDependency(descriptor).apply(block)
}

/**
 * Adds a plugin that matches the specified descriptor [name] by adding
 * it to the plugin's classpath and setting it as the plugin's pre-boot priority.
 * @param name the dependency name.
 */
@UnstableKatanApi
inline fun PluginDependencyManager.dependsOn(
    name: String,
    crossinline block: PluginDependency.() -> Unit = {}
): PluginDependency {
    return dependsOn(PluginDescriptor(name), block)
}

/**
 * Adds a plugin that matches the specified descriptor [name] and [version] by adding
 * it to the plugin's classpath and setting it as the plugin's pre-boot priority.
 * @param name the dependency name.
 * @param version the dependency version.
 */
@UnstableKatanApi
inline fun PluginDependencyManager.dependsOn(
    name: String,
    version: CharSequence,
    crossinline block: PluginDependency.() -> Unit = {}
): PluginDependency {
    return dependsOn(PluginDescriptor(name, Version(version)), block)
}

/**
 * Adds a plugin that matches the specified descriptor [name] and [version] by adding
 * it to the plugin's classpath and setting it as the plugin's pre-boot priority.
 * @param name the dependency name.
 * @param version the dependency version.
 */
@UnstableKatanApi
inline fun PluginDependencyManager.dependsOn(
    name: String,
    version: Version,
    crossinline block: PluginDependency.() -> Unit = {}
): PluginDependency {
    return dependsOn(PluginDescriptor(name, version), block)
}