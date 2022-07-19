package org.katan.config

interface KatanConfig {

    val nodeId: Int

    val database: DatabaseConfig

    val server: HttpServerConfig

    val docker: DockerClientConfig

    interface DatabaseConfig {

        val connectionString: String

    }

    interface HttpServerConfig {

        val port: Int

    }

    interface DockerClientConfig {

        val path: String

    }

}