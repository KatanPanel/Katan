package org.katan.config

import kotlin.time.Duration

interface KatanConfig {

    val nodeId: Int

    val database: DatabaseConfig

    val server: HttpServerConfig

    val docker: DockerClientConfig

    val redis: RedisConfig

    interface DatabaseConfig {

        val connectionString: String

    }

    interface HttpServerConfig {

        val port: Int

    }

    interface DockerClientConfig {

        val host: String

        val network: DockerNetworkConfig

    }

    interface DockerNetworkConfig {

        val name: String

        val driver: String

    }

    interface RedisConfig {

        val host: String?

        val port: Int?

        val username: String?

        val password: String?

        val connectionTimeout: Duration?

        val soTimeout: Duration?

        val clusters: List<RedisClusterConfig>

        val database: Int?

    }

    interface RedisClusterConfig {

        val host: String?

        val port: Int?

    }

}