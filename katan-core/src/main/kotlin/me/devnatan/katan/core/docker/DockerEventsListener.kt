package me.devnatan.katan.core.docker

import br.com.devsrsouza.kotlin.docker.apis.SystemApi
import com.github.dockerjava.api.async.ResultCallback
import com.github.dockerjava.api.model.Event
import com.github.dockerjava.api.model.EventType
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

@OptIn(ExperimentalCoroutinesApi::class)
class DockerEventsListener(private val core: KatanCore): CoroutineScope by CoroutineScope(CoroutineName("DockerEvents")) {

    companion object {

        private val log: Logger = logger<DockerEventsListener>()

    }

    private val systemApi = SystemApi(serializer = Json {
        isLenient = true
    })

    suspend fun listen() {
        systemApi.systemEvents(null, null, "{\"type\": [\"container\"]}").collect { event ->
            val serverId = event.actor?.attributes?.get("katan.server.id") ?: return@collect
            val action = event.action!!
            log.debug("$action: $serverId")

            when (action) {
                "start" -> serverStarted(serverId.toInt(), event.time!!.toLong())
                "stop" -> serverStopped(serverId.toInt(), event.time!!.toLong())
                "pause", "unpause", "Kill", "die", "oom" -> {
                    // update server status
                    inspectServer(serverId.toInt())
                }
            }
        }
    }

    private suspend inline fun inspectServer(serverId: Int) {
        core.serverManager.inspectServer(core.serverManager.getServer(serverId))
    }

    private suspend fun serverStarted(serverId: Int, timestamp: Long) {
        val server = core.serverManager.getServer(serverId)
        log.info("Server ${server.name} started.")

        core.eventBus.publish(ServerStartEvent(server, duration = Duration.ofMillis(Instant.now().toEpochMilli() - timestamp)))
        core.serverManager.inspectServer(server)
    }

    private suspend fun serverStopped(serverId: Int, timestamp: Long) {
        val server = core.serverManager.getServer(serverId)
        log.info("Server ${server.name} stopped.")
        core.eventBus.publish(ServerStopEvent(server, duration = Duration.ofMillis(Instant.now().toEpochMilli() - timestamp)))
        core.serverManager.inspectServer(server)
    }

}