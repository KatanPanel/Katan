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

package me.devnatan.katan.api.cache

/**
 * Base interface for implementing caching services such
 * as local cache, Redis, in memory, and others.
 */
interface Cache<V> {

    companion object {

        const val KEY_PREFIX = "katan_"

    }

    /**
     * Get the cached value of the specified [key].
     * @param key the key to be searched.
     * @throws NoSuchElementException if the key does not exist.
     */
    fun get(key: String): V

    /**
     * Sets the cached [value] for this [key].
     * @param key the key to be set.
     * @param value the value of the key.
     */
    fun set(key: String, value: V)

    /**
     * Checks whether the key exists in the caching service.
     * @param key the key to be verified.
     */
    fun has(key: String): Boolean

    /**
     * Whether the caching service is available to be used.
     */
    fun isAvailable(): Boolean

    /**
     * Terminates the execution of the caching service if available.
     */
    suspend fun close()

}

/**
 * An empty caching provider, with no functionality and unavailable for use.
 */
class UnavailableCacheProvider<V> : Cache<V> {

    override fun isAvailable() = false

    override fun get(key: String): V {
        throw UnsupportedOperationException()
    }

    override fun set(key: String, value: V) {
        throw UnsupportedOperationException()
    }

    override fun has(key: String): Boolean {
        throw UnsupportedOperationException()
    }

    override suspend fun close() {
    }

}