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

package me.devnatan.katan.api.server.composition

/**
 * Represents the container of all the compositions present in a [Server].
 */
interface Compositions : Iterable<CompositionStore<*>> {

    /**
     * Returns the composition with the provided [key]
     * for this instance or `null` if no composition is found.
     */
    operator fun <T : CompositionOptions> get(key: Composition.Key): CompositionStore<T>?

    /**
     * Defines the value of a [key] for the specified [composition].
     */
    operator fun set(key: Composition.Key, composition: CompositionStore<*>)

}