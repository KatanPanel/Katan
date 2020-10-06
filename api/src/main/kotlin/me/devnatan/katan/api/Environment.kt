package me.devnatan.katan.api

import org.slf4j.event.Level

inline class KatanEnvironment(private val value: String) {

    companion object {

        const val LOCAL = "local"
        const val DEVELOPMENT = "dev"
        const val TESTING = "test"
        const val STAGING = "stage"
        const val PRODUCTION = "prod"

        /**
         * Returns all available development modes
         */
        val ALL: Array<String> by lazy {
            arrayOf(LOCAL, DEVELOPMENT, TESTING, STAGING, PRODUCTION)
        }

    }

    /**
     * Returns `true` if the current environment mode is in [DEVELOPMENT].
     */
    fun isLocal(): Boolean {
        return value == LOCAL
    }

    /**
     * Returns `true` if the current environment mode is in [DEVELOPMENT] or [LOCAL].
     */
    fun isDevelopment(): Boolean {
        return value == LOCAL || value == DEVELOPMENT
    }

    /**
     * Returns `true` if the current environment mode is in [TESTING] or [STAGING].
     */
    fun isTesting(): Boolean {
        return value == TESTING || value == STAGING
    }

    /**
     * Returns `true` if the current environment mode is in [PRODUCTION].
     */
    fun isProduction(): Boolean {
        return value == PRODUCTION
    }

    override fun toString(): String {
        return value
    }

}

/**
 * Returns the default recommended logging level for this environment mode.
 */
fun KatanEnvironment.defaultLogLevel(): Level = when {
    isLocal() -> Level.TRACE
    isDevelopment() || isTesting() -> Level.DEBUG
    else -> Level.INFO
}