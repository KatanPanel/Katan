package me.devnatan.katan.api.cli

import org.slf4j.event.Level

class CommandException(message: String, cause: Throwable?) : RuntimeException(message, cause)

class FriendlyCommandException(message: String, val level: Level) : Exception(message)