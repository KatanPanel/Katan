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

import me.devnatan.katan.api.server.Server

/**
 *
 */
interface Composition<out T : CompositionOptions> {

    companion object {

        fun Key(name: String): Key = KeyImpl(name)

    }

    interface Key {

        val name: String

    }

    /**
     * Returns the key for that composition.
     */
    val key: Key

    /**
     * Reads this composition for a specific [server],
     * defining its options throughout the life cycle of the server.
     */
    suspend fun read(server: Server, store: CompositionStore<@UnsafeVariance T>, factory: CompositionFactory) {
    }

    /**
     * Writes the values of this composition to the [server].
     */
    suspend fun write(server: Server, store: CompositionStore<@UnsafeVariance T>, factory: CompositionFactory) {
    }

}

private inline class KeyImpl(override val name: String) : Composition.Key