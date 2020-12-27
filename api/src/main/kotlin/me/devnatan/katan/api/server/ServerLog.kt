package me.devnatan.katan.api.server

import java.time.Instant

data class ServerLog(
    val type: ServerLogType,
    val content: String,
    private val plainTimestamp: String
) {

    val timestamp: Instant
        get() = Instant.parse(plainTimestamp)

}