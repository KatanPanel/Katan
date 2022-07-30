package org.katan.config

import com.sksamuel.hoplite.ConfigAlias
import kotlin.time.Duration

internal data class ConfigImpl(
    override val nodeId: Int,
    override val database: DatabaseConfigImpl,
    override val server: HttpServerConfigImpl,
    override val docker: DockerClientConfigImpl,
    override val redis: RedisConfigImpl
) : KatanConfig {

    data class DatabaseConfigImpl(
        @ConfigAlias("uri") override val connectionString: String
    ) : KatanConfig.DatabaseConfig

    data class HttpServerConfigImpl(
        override val port: Int
    ) : KatanConfig.HttpServerConfig

    data class DockerClientConfigImpl(
        override val host: String,
        override val network: DockerNetworkConfigImpl
    ) : KatanConfig.DockerClientConfig

    data class DockerNetworkConfigImpl(
        override val driver: String
    ) : KatanConfig.DockerNetworkConfig {

        override val name: String get() = "katan0"

    }

    data class RedisConfigImpl(
        override val host: String,
        override val port: Int
    ) : KatanConfig.RedisConfig {

        override val username: String? = null
        override val password: String? = null
        override val connectionTimeout: Duration? = null
        override val soTimeout: Duration? = null
        override val database: Int? = null
        override val clusters: List<KatanConfig.RedisClusterConfig> = emptyList()

    }

    data class RedisClusterConfigImpl(
        override val host: String,
        override val port: Int
    ) : KatanConfig.RedisClusterConfig

}