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
 * Represents a phase during the initialization period of a plugin.
 * @property key a key for this phase
 */
inline class PluginPhase(val key: String) {

    override fun toString(): String {
        return "Phase($key)"
    }

}

/**
 * Represents a handler for the [PluginPhase], unlike events only
 * occur within the scope of the plugin, that is, it is the plugin for itself.
 */
interface PluginHandler {

    /**
     * Called when a [PluginPhase] occurs.
     */
    suspend fun handle()

}

/**
 * Inline implementation for [PluginHandler].
 */
internal class PluginHandlerImpl(inline val handler: suspend () -> Unit) : PluginHandler {

    override suspend fun handle() {
        handler()
    }

}

/**
 * Phase called when the plugin is loaded.
 */
val PluginLoaded = PluginPhase("PluginLoaded")

/**
 * Phase called when the plugin is started.
 */
val PluginEnabled = PluginPhase("PluginEnabled")

/**
 * Phase called when the plugin is stopped.
 */
val PluginDisabled = PluginPhase("PluginDisabled")

/**
 * Phase called when Katan starts the boot process.
 */
val KatanInit = PluginPhase("KatanInit")

/**
 * Phase called when the Katan is completely started.
 */
val KatanStarted = PluginPhase("KatanStarted")