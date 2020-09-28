package me.devnatan.katan.api.cache

interface Cache<V> {

    /**
     * Get the value of the specified [key].
     * @param key the key to be searched
     * @throws NoSuchElementException if the key does not exist
     */
    fun get(key: String): V

    /**
     * Set the string value as value of the key
     * @param key the key to be set
     * @param value the key value
     */
    fun set(key: String, value: V)

    /**
     * Checks whether the key exists in the caching service.
     * @param key the key to be verified
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
     * @throws IllegalStateException if it is not running
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

    override fun <T> unsafe(): T {
        throw UnsupportedOperationException()
    }

    override suspend fun close() {
        throw UnsupportedOperationException()
    }

}