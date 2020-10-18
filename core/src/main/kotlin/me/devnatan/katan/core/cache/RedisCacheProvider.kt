package me.devnatan.katan.core.cache

import kotlinx.coroutines.*
import me.devnatan.katan.api.cache.Cache
import redis.clients.jedis.Jedis
import redis.clients.jedis.JedisPool
import redis.clients.jedis.Pipeline

class RedisCacheProvider(private val pool: JedisPool) : Cache<Any> {

    init {
        // check if the redis has been successfully connected
        // tries to get a resource from the pool, will throw an exception if not connected
        pool.resource
    }

    override fun isAvailable(): Boolean {
        return !pool.isClosed
    }

    override fun <T> unsafe(): T {
        return pool.resource as T
    }

    override fun get(key: String): String {
        return pool.resource.use {
            it.get(key)
        } ?: throw NoSuchElementException(key)
    }

    override fun set(key: String, value: Any) {
        return pool.resource.use {
            if (value !is String)
                throw IllegalArgumentException("Value must be String")

            it.set(key, value)
        }
    }

    override fun has(key: String): Boolean {
        return pool.resource.use {
            it.exists(key)
        }
    }

    override suspend fun close() {
        pool.close()
    }

}

/**
 * Asynchronously executes a list of commands at once.
 * @param pipe the execution pipe
 */
@ExperimentalCoroutinesApi
suspend fun <V> Cache<V>.asyncPipeline(
    coroutineScope: CoroutineScope,
    pipe: Pipeline.() -> Unit
) = (unsafe() as Jedis).use { redis ->
    val pipeline = redis.pipelined().apply(pipe)
    coroutineScope.async(Dispatchers.Unconfined, CoroutineStart.ATOMIC) {
        withContext(Dispatchers.IO) {
            pipeline.sync()
        }
        pipeline
    }
}