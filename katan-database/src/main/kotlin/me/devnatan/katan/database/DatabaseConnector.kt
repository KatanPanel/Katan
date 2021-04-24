package me.devnatan.katan.database

import kotlinx.coroutines.CoroutineName
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.withContext
import kotlin.time.ExperimentalTime
import kotlin.time.TimeSource

interface DatabaseConnector {

    companion object {
        const val COROUTINE_NAME = "Katan Database"
    }

    val queryHandler: DatabaseQueryHandler

    val repositories: DatabaseRepositories

    suspend fun connect(settings: DatabaseSettings)

    suspend fun close()

    fun isUrlSupported(url: String): Boolean

    fun isDialectSupported(dialect: String): Boolean

    fun isConnected(): Boolean

    @OptIn(ExperimentalTime::class)
    suspend fun <T> wrap(query: String, scope: suspend CoroutineScope.() -> T): T {
        val mark = TimeSource.Monotonic.markNow()

        return runCatching {
            withContext(SupervisorJob().also { job ->
                job.invokeOnCompletion { error ->
                    val elapsed = mark.elapsedNow()
                    if (error != null)
                        queryHandler.error(query, elapsed, error)
                    else
                        queryHandler.completed(query, elapsed)
                }
            } + CoroutineName("$COROUTINE_NAME @ $query"), scope)
        }.onFailure { error ->
            queryHandler.error(query, mark.elapsedNow(), error)
        }.getOrThrow()
    }

}