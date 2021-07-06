/*
 * Copyright 2020-present Natan Vieira do Nascimento
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package me.devnatan.katan.api.plugin

/**
 * Responsible for loading, unloading, enabling and stopping [Plugin]s and their dependencies.
 */
interface PluginManager {

    /**
     * Returns all registered plugins.
     */
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