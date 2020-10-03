package me.devnatan.katan.api.manager

import me.devnatan.katan.api.plugin.KatanPlugin
import me.devnatan.katan.api.plugin.PluginDescriptor

interface PluginManager {

    /**
     * Returns a plugin that has the same descriptions as the
     * specified [descriptor] or null if it is not registered.
     * @param descriptor the matching descriptor
     */
    fun getPlugin(descriptor: PluginDescriptor): KatanPlugin?

    /**
     * Initializes a plugin that has the same descriptions as the specified descriptor.
     * @param descriptor the matching descriptor
     */
    suspend fun initializePlugin(descriptor: PluginDescriptor): KatanPlugin

    /**
     * Stops a plugin that has the same descriptions as the specified descriptor,
     * returning the plugin instance or null if the plugin has not been found.
     * @param descriptor the matching descriptor
     */
    suspend fun stopPlugin(descriptor: PluginDescriptor): KatanPlugin?

}