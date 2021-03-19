package me.devnatan.katan.core.cache

import me.devnatan.katan.api.cache.Cache
import me.devnatan.katan.api.cache.Cache.Companion.KEY_PREFIX
import redis.clients.jedis.JedisPool

class RedisCacheProvider(private val pool: JedisPool) : Cache<Any> {

    init {
        // check if the redis has been successfully connected tries to get
        // a resource from the pool, will throw an exception if not connected
        pool.resource
    }

    override fun isAvailable(): Boolean {
        return !pool.isClosed
    }

    override fun get(key: String): String {
        return pool.resource.use {
            it.get(KEY_PREFIX + key)
        } ?: throw NoSuchElementException(key)
    }

    override fun set(key: String, value: Any) {
        return pool.resource.use {
            if (value !is String)
                throw IllegalArgumentException("Value must be String")

            it.set(KEY_PREFIX + key, value)
        }
    }

    override fun has(key: String): Boolean {
        return pool.resource.use {
            it.exists(KEY_PREFIX + key)
        }
    }

    override suspend fun close() {
        pool.close()
    }

}