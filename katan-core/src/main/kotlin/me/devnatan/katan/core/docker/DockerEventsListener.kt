package me.devnatan.katan.core.docker

import br.com.devsrsouza.kotlin.docker.apis.SystemApi
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.collect
import kotlinx.serialization.json.Json
import me.devnatan.katan.api.event.server.ServerStartEvent
import me.devnatan.katan.api.event.server.ServerStopEvent
import me.devnatan.katan.api.logging.logger
import me.devnatan.katan.core.KatanCore
import org.slf4j.Logger
import java.time.Duration
import java.time.Instant
import java.util.concurrent.Executors

class DockerEventsListener(private val core: KatanCore): CoroutineScope by CoroutineScope(CoroutineName("DockerEvents")) {

    companion object {

        private val log: Logger = logger<DockerEventsListener>()

    }

    private val systemApi = SystemApi(serializer = Json {
        ignoreUnknownKeys = true
    })

    fun listen() = launch(Executors.newSingleThreadExecutor().asCoroutineDispatcher()) {
        systemApi.systemEvents(null, null, "{\"type\": [\"container\"]}").collect { event ->
            val serverId = event.actor?.attributes?.get("katan.server.id") ?: return@collect
            val action = event.action!!
            log.debug("$action: $serverId")

            when (action) {
                "start" -> serverStarted(serverId.toInt(), event.time!!.toLong())
                "stop" -> serverStopped(serverId.toInt(), event.time!!.toLong())
                "pause", "unpause", "kill", "die", "oom" -> {
                    // update server status
                    core.serverManager.inspectServer(core.serverManager.getServer(serverId.toInt()))
                }
            }
        }
    }

    private fun serverStarted(serverId: Int, timestamp: Long) = launch(Dispatchers.IO) {
        val server = core.serverManager.getServer(serverId)
        log.info("Server ${server.name} started.")
        core.eventBus.publish(ServerStartEvent(server, duration = Duration.ofMillis(Instant.now().toEpochMilli() - timestamp)))
        core.serverManager.inspectServer(server)
    }

    private fun serverStopped(serverId: Int, timestamp: Long) = launch(Dispatchers.IO) {
        val server = core.serverManager.getServer(serverId)
        log.info("Server ${server.name} stopped.")
        core.eventBus.publish(ServerStopEvent(server, duration = Duration.ofMillis(Instant.now().toEpochMilli() - timestamp)))
        core.serverManager.inspectServer(server)
    }

}