package me.devnatan.katan.api.command

import org.slf4j.event.Level

interface CommandExecutionContext {

    val label: String

    val args: Array<out String>

}

fun CommandExecutionContext.log(message: String, level: Level = Level.INFO) {
    throw FriendlyCommandException(message, level)
}

fun CommandExecutionContext.fail(message: String, cause: Throwable? = null): Nothing {
    throw CommandException(message, cause)
}