package org.katan

import org.apache.logging.log4j.LogManager
import org.koin.core.Koin
import org.koin.core.logger.Level
import org.koin.core.logger.MESSAGE
import org.koin.core.logger.Level as KoinLogLevel
import org.koin.core.logger.Logger as KoinLogger

typealias Log4jLogger = org.apache.logging.log4j.Logger
typealias Log4jLogLevel = org.apache.logging.log4j.Level

/**
 * Implementation of Koin's Logger backed by a Log4j2 Logger
 */
internal class KoinLog4jLogger : KoinLogger(logLevel) {

    companion object {
        private val backingLogger: Log4jLogger = LogManager.getLogger(Koin::class.java)

        @JvmStatic
        val logLevel: KoinLogLevel
            get() {
                val isDevelopmentMode = runCatching { System.getenv(KatanConfig.ENV) }
                    .getOrNull()
                    .orEmpty() == KatanConfig.DEVELOPMENT

                return if (isDevelopmentMode) {
                    KoinLogLevel.DEBUG
                } else {
                    KoinLogLevel.INFO
                }
            }
    }

    override fun display(level: Level, msg: MESSAGE) {
        backingLogger.log(transformLevel(level), msg)
    }

    /**
     * Transform a Koin log level to Log4J log level.
     * @param level The Koin log level.
     */
    private fun transformLevel(level: KoinLogLevel): Log4jLogLevel {
        return when (level) {
            KoinLogLevel.DEBUG -> Log4jLogLevel.DEBUG
            KoinLogLevel.INFO -> Log4jLogLevel.INFO
            KoinLogLevel.ERROR -> Log4jLogLevel.ERROR
            else -> Log4jLogLevel.ALL
        }
    }
}
