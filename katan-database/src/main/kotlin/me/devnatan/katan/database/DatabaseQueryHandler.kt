package me.devnatan.katan.database

import kotlin.time.Duration
import kotlin.time.ExperimentalTime

@OptIn(ExperimentalTime::class)
interface DatabaseQueryHandler {

    fun completed(query: String, duration: Duration)

    fun error(query: String, duration: Duration, error: Throwable)

}