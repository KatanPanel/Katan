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

import me.devnatan.katan.api.Descriptor

/**
 * Dependency of a plugin, described through its [descriptor], which is the
 * same as the plugin.
 *
 * Dependencies are loaded with the plugin's classloader during the plugin's
 * initialization, if there are non-[isOptional] dependencies that are not
 * loaded for the plugin during its initialization they will veto the
 * continued loading of the plugin.
 *
 * @author Natan Vieira
 * @since  1.0
 */
open class PluginDependency(val descriptor: Descriptor) {

    var value: (() -> Any?)? = null

    /**
     * If this dependency is not required during the plugin's initialization.
     * Non-optional dependencies prevent the plugin from starting if they
     * are not loaded.
     */
    var isOptional: Boolean = false
    private val exclusions: MutableSet<String> = hashSetOf()

    /**
     * Excludes the dependency from this dependency, that is, it will not be
     * loaded into the plugin's class loader, this is good for avoiding
     * version conflicts for example.
     * @param dependency dependency name
     */
    fun exclude(dependency: String): PluginDependency {
        exclusions.add(dependency)
        return this
    }

    /**
     * Reverses the value if this dependency is optional or not.
     * Default value: false
     */
    fun optional(): PluginDependency {
        isOptional = !isOptional
        return this
    }

}