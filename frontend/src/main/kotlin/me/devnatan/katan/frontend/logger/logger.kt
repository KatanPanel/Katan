package me.devnatan.katan.frontend.logger

import me.devnatan.katan.frontend.LogLevel
import me.devnatan.katan.frontend.log

class Logger(private val prefix: String = "Application") {

    fun log(message: String) {
        log(message, LogLevel.DEBUG, prefix)
    }

    fun fine(message: String) {
        log(message, LogLevel.FINE, prefix)
    }

    fun info(message: String) {
        log(message, LogLevel.INFO, prefix)
    }

    fun warn(message: String) {
        log(message, LogLevel.WARN, prefix)
    }

    fun err(message: String) {
        log(message, LogLevel.ERROR, prefix)
    }

}