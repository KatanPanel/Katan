package org.katan.config

class KatanConfig internal constructor() {

    companion object {
        private const val DEFAULT_PORT = 8080
    }

    val port: Int = env("PORT")?.toIntOrNull() ?: DEFAULT_PORT
    val dockerHost: String = env("DOCKER_HOST").orEmpty()
    val databaseHost: String = envOrThrow("DB_HOST")
    val databaseUser: String = env("DB_USER").orEmpty()
    val databasePassword: String = env("DB_PASS").orEmpty()

    @Suppress("NOTHING_TO_INLINE")
    private inline fun env(name: String): String? {
        return System.getenv(name)
    }

    @Suppress("NOTHING_TO_INLINE")
    private inline fun envOrThrow(name: String): String {
        return env(name) ?: error("Missing required environment variable: $name")
    }

}
