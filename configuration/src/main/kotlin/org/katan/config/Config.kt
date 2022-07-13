package org.katan.config

import com.sksamuel.hoplite.ConfigAlias

internal data class ConfigImpl(
    override val nodeId: Int,
    override val database: DatabaseConfig,
    override val server: HttpServerConfig
) : KatanConfig {

    data class DatabaseConfig(
        @ConfigAlias("uri") override val connectionString: String
    ) : KatanConfig.DatabaseConfig

    data class HttpServerConfig(
        override val port: Int
    ) : KatanConfig.HttpServerConfig

}