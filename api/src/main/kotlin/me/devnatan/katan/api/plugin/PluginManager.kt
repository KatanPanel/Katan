package me.devnatan.katan.api.plugin

/**
 * Responsible for loading, unloading, enabling and stopping plugins and their dependencies.
 */
interface PluginManager {

    fun getPlugins(): List<Plugin>

    /**
     * Returns a plugin that has the same descriptions as the
     * specified [descriptor] or null if it is not registered.
     * @param descriptor the matching descriptor.
     */
    fun getPlugin(descriptor: PluginDescriptor): Plugin?

    /**
     * Loads a plugin that has the same descriptions as the specified descriptor.
     * @param descriptor the matching descriptor.
     * @throws IllegalStateException if the plugin has already been loaded.
     */
    suspend fun loadPlugin(descriptor: PluginDescriptor): Plugin

    /**
     * Unloads a previously loaded plugin, obtained through [loadPlugin].
     * @param plugin the plugin to be unloaded.
     * @throws IllegalStateException if the plugin has not been loaded
     */
    suspend fun unloadPlugin(plugin: Plugin): Plugin

    /**
     * Starts a plugin that is already initialized.
     * @param plugin the plugin to be started.
     * @throws IllegalStateException if the plugin is already started.
     */
    suspend fun startPlugin(plugin: Plugin)

    /**
     * Stops a plugin that has the same descriptions as the specified descriptor,
     * returning the plugin instance or null if the plugin has not been found.
     * @param plugin the plugin to be stopped.
     * @throws IllegalStateException if the plugin has not been started.
     */
    suspend fun stopPlugin(plugin: Plugin): Plugin?

}