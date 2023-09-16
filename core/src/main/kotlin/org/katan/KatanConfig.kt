package org.katan

public data class KatanConfig internal constructor(
    val env: String = env(ENV, DEVELOPMENT),
    val version: String = env("VERSION") ?: "Unknown",
    val gitBranch: String? = env("GIT_BRANCH"),
    val gitCommit: String? = env("GIT_COMMIT"),
    val nodeId: Int = env("NODE_ID", "0").toInt(),
    val host: String = env("HOST", "0.0.0.0"),
    val port: Int = env("PORT", "0").toInt(),
    val dockerHost: String = env("DOCKER_HOST", "unix:///var/run/docker.sock"),
    val dockerNetwork: String = env("DOCKER_NETWORK", "katan0"),
    val databaseHost: String = env("DB_HOST", "localhost"),
    val databaseUser: String = env("DB_USER", ""),
    val databasePassword: String = env("DB_PASS", ""),
    val redisUser: String = env("REDIS_USER", "default"),
    val redisPassword: String = env("REDIS_PASS").orEmpty(),
    val redisHost: String? = env("REDIS_HOST"),
    val redisPort: String? = env("REDIS_PORT"),
    val isDevelopment: Boolean = env == DEVELOPMENT,
) {

    public companion object {
        public const val ENV: String = "ENV"
        public const val DEVELOPMENT: String = "dev"
        public const val PRODUCTION: String = "prod"

        private fun env(name: String): String? = System.getenv(name)

        private fun env(name: String, defaultValue: String) = System.getenv(name) ?: defaultValue
    }

    init {
        require(env == DEVELOPMENT || env == PRODUCTION) {
            "Invalid environment variable value: $env"
        }
    }
}
