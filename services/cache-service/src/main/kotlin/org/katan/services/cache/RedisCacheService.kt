package org.katan.services.cache

import org.apache.logging.log4j.LogManager
import org.katan.KatanConfig
import redis.clients.jedis.DefaultJedisClientConfig
import redis.clients.jedis.HostAndPort
import redis.clients.jedis.JedisCluster
import redis.clients.jedis.Protocol
import redis.clients.jedis.UnifiedJedis
import java.io.Closeable

internal class RedisCacheService(private val config: KatanConfig) : CacheService, Closeable {

    companion object {

        private val logger = LogManager.getLogger(RedisCacheService::class.java)
    }

    private var client: UnifiedJedis? = null

    override suspend fun get(key: String): String = pool { resource -> resource.get(key) }

    override suspend fun set(key: String, value: String): String = pool { resource -> resource.set(key, value) }

    private inline fun <T> pool(block: (UnifiedJedis) -> T): T {
        if (client == null)
            client = initClient()

        return client!!.use(block)
    }

    private fun initClient(): UnifiedJedis {
        logger.info("Initializing Redis client...")

        val clientConfig = DefaultJedisClientConfig.builder()
            .connectionTimeoutMillis(Protocol.DEFAULT_TIMEOUT)
            .timeoutMillis(Protocol.DEFAULT_TIMEOUT)
            .user(config.redisUser)
            .password(config.redisPassword)
            .ssl(false)
            .database(Protocol.DEFAULT_DATABASE)
            .clientName("Katan")
            .build()

//        if (redisConfig.clusters.isEmpty()) {
//            val addr = HostAndPort(
//                redisConfig.host ?: Protocol.DEFAULT_HOST,
//                redisConfig.port ?: Protocol.DEFAULT_PORT
//            )
//
//            logger.info("Redis: $addr")
//            return JedisPooled(
//                addr,
//                clientConfig
//            )
//        }

        val nodes = setOf(
            HostAndPort(
                config.redisHost ?: Protocol.DEFAULT_HOST,
                config.redisPort?.toIntOrNull() ?: Protocol.DEFAULT_PORT
            )
        )
//        logger.debug("Jedis cluster nodes (${nodes.size}):")
//        logger.debug(nodes.joinToString(", "))

        return JedisCluster(
            nodes,
            clientConfig
        )
    }

    override fun close() {
        client?.close()
        logger.debug("Redis client closed.")
    }
}
