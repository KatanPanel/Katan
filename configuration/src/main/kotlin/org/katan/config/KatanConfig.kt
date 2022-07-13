package org.katan.config

interface KatanConfig {

    val nodeId: Int

    val database: DatabaseConfig

    val server: HttpServerConfig

    interface DatabaseConfig {

        val connectionString: String

    }

    interface HttpServerConfig {

        val port: Int

    }

}