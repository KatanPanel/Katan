package org.katan.config

class KatanConfig internal constructor() {

    companion object {
        private const val DEFAULT_HOST = "0.0.0.0"
        private const val DEFAULT_PORT = 80
        private const val DEFAULT_REDIS_USER = "default"
        private const val DEFAULT_DOCKER_HOST = "unix:///var/run/docker.sock"
        private const val DEFAULT_DOCKER_NET = "katan0"
        private const val DEFAULT_DB_HOST = "localhost"

        const val ENV = "ENV"
        const val DEVELOPMENT = "dev"
    }

    @Suppress("MemberVisibilityCanBePrivate")
    val env: String = env(ENV) ?: DEVELOPMENT
    val version: String = env("VERSION") ?: "Unknown"
    val gitBranch: String? = env("GIT_BRANCH")
    val gitCommit: String? = env("GIT_COMMIT")

    val nodeId: Int = env("NODE_ID")?.toIntOrNull() ?: 0
    val host: String = env("HOST") ?: DEFAULT_HOST
    val port: Int = env("PORT")?.toIntOrNull() ?: DEFAULT_PORT

    val dockerHost: String = env("DOCKER_HOST") ?: DEFAULT_DOCKER_HOST
    val dockerNetwork: String = env("DOCKER_NETWORK") ?: DEFAULT_DOCKER_NET

    val databaseHost: String = env("DB_HOST") ?: DEFAULT_DB_HOST
    val databaseUser: String = env("DB_USER").orEmpty()
    val databasePassword: String = env("DB_PASS").orEmpty()

    val redisUser: String = env("REDIS_USER") ?: DEFAULT_REDIS_USER
    val redisPassword: String = env("REDIS_PASS").orEmpty()
    val redisHost: String? = env("REDIS_HOST")
    val redisPort: String? = env("REDIS_PORT")

    val isDevelopment: Boolean get() = env == DEVELOPMENT

    @Suppress("NOTHING_TO_INLINE")
    private inline fun env(name: String): String? {
        return System.getenv(name)
    }

    @Suppress("NOTHING_TO_INLINE")
    private inline fun envOrThrow(name: String): String {
        return env(name) ?: error("Missing required environment variable: $name")
    }
}
