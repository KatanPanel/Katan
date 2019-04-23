package me.devnatan.katan.frontend

enum class LogLevel {

    DEBUG, FINE, INFO, WARN, ERROR

}

fun log(message: String, level: LogLevel = LogLevel.DEBUG, prefix: String = "Application") {
    val args = arrayOf("%c[$prefix] %c$message", when (level) {
        LogLevel.FINE -> "color: #20bf6b"
        LogLevel.INFO -> "color: #3867d6"
        LogLevel.WARN -> "color: #f7b731"
        LogLevel.ERROR -> "color: #eb3b5a"
        else -> "color: #8854d0"
    }, "color: black")

    when (level) {
        LogLevel.INFO -> console.info(*args)
        LogLevel.WARN -> console.warn(*args)
        LogLevel.ERROR -> console.error(*args)
        else -> console.log(*args)
    }
}