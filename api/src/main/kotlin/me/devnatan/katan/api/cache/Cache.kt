package me.devnatan.katan.api.cache

/**
 * Base interface for implementing caching services such as local cache, Redis, in memory, and others.
 */
interface Cache<V> {

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
     * Returns the class for extensive or non-secure methods.
     */
    fun <T> unsafe(): T

    /**
     * Terminates the execution of the caching service if available.
     * @throws IllegalStateException if it is not running.
     */
    suspend fun close()

}

/**
 * An empty caching provider, with no functionality and unavailable for use.
 */
class UnavailableCacheProvider<V> : Cache<V> {

    override fun isAvailable() = false

    private fun unavailable(): Nothing = throw UnsupportedOperationException()

    override fun get(key: String): V = unavailable()

    override fun set(key: String, value: V) = unavailable()

    override fun has(key: String): Boolean {
        return isAvailable()
    }

    override fun <T> unsafe(): T = unavailable()

    override suspend fun close() = unavailable()

}