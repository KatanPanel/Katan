package org.katan.config

import com.sksamuel.hoplite.ConfigAlias

internal data class ConfigImpl(
    override val nodeId: Int,
    override val database: DatabaseConfig,
    override val server: HttpServerConfig,
    override val docker: DockerClientConfig
) : KatanConfig {

    data class DatabaseConfig(
        @ConfigAlias("uri") override val connectionString: String
    ) : KatanConfig.DatabaseConfig

    data class HttpServerConfig(
        override val port: Int
    ) : KatanConfig.HttpServerConfig

    data class DockerClientConfig(
        override val host: String
    ) : KatanConfig.DockerClientConfig

}