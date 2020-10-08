package me.devnatan.katan.api.manager

import me.devnatan.katan.api.plugin.Plugin
import me.devnatan.katan.api.plugin.PluginDescriptor

interface PluginManager {

    /**
     * Returns a copy of all registered accounts.
     */
    fun getPlugins(): List<Plugin>

    /**
     * Returns a plugin that has the same descriptions as the
     * specified [descriptor] or null if it is not registered.
     * @param descriptor the matching descriptor
     */
    fun getPlugin(descriptor: PluginDescriptor): Plugin?

    /**
     * Loads a plugin that has the same descriptions as the specified descriptor.
     * @throws IllegalStateException if the plugin has already been loaded
     * @param descriptor the matching descriptor
     */
    suspend fun loadPlugin(descriptor: PluginDescriptor): Plugin

    /**
     * Unloads a previously loaded plugin, obtained through [loadPlugin]
     * @throws IllegalStateException if the plugin has not been loaded
     * @param plugin the plugin to be unloaded
     */
    suspend fun unloadPlugin(plugin: Plugin): Plugin

    /**
     * Starts a plugin that is already initialized, obtained through [initializePlugin].
     * @throws IllegalStateException if the plugin is already started
     * @param plugin the plugin to be started
     */
    suspend fun startPlugin(plugin: Plugin)

    /**
     * Stops a plugin that has the same descriptions as the specified descriptor,
     * returning the plugin instance or null if the plugin has not been found.
     * @throws IllegalStateException if the plugin has not been started
     * @param plugin the plugin to be stopped
     */
    suspend fun stopPlugin(plugin: Plugin): Plugin?

}