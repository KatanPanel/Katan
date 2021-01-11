package me.devnatan.katan.core.docker

import com.github.dockerjava.api.async.ResultCallback
import com.github.dockerjava.api.model.Event
import com.github.dockerjava.api.model.EventType
import kotlinx.coroutines.*
import me.devnatan.katan.api.event.ServerStartedEvent
import me.devnatan.katan.api.event.ServerStoppedEvent
import me.devnatan.katan.api.logging.logger
import me.devnatan.katan.api.server.Server
import me.devnatan.katan.core.KatanCore
import org.slf4j.Logger
import java.io.Closeable
import java.time.Duration
import java.time.Instant
import java.util.concurrent.Executors

@OptIn(ExperimentalCoroutinesApi::class)
class DockerEventsListener(private val core: KatanCore): CoroutineScope by CoroutineScope(CoroutineName("DockerEvents")),
    Closeable {

    companion object {

        private val logger: Logger = logger<DockerEventsListener>()

    }

    private var callback: ResultCallback<Event>? = null
    private val dispatcher = Executors.newSingleThreadExecutor().asCoroutineDispatcher()

    fun listen() {
        callback = object: ResultCallback.Adapter<Event>() {
            override fun onNext(event: Event) {
                val container = event.actor!!.id!!
                val action = event.action!!
                logger.debug("[Event] $action: $container")

                when (action) {
                    "start" -> launchLocally(action) { onServerStart(container, event.time) }
                    "stop" -> launchLocally(action) { onServerStop(container, event.time) }
                    "pause", "unpause", "kill", "die", "oom" -> launchLocally(action) {
                        inspectServerByContainerId(container)
                    }
                    else -> { /* ignore */ }
                }
            }

            override fun onComplete() {
                // it shouldn't happen, probably timeout.
                // TODO: handle cancellations
            }

            override fun onError(e: Throwable) {
               logger.error("Uncaught error at Docker Events Listener", e)
            }
        }

        core.docker.client.eventsCmd().withEventTypeFilter(EventType.CONTAINER).exec(callback)
    }

    private fun launchLocally(action: String, block: suspend CoroutineScope.() -> Unit): Job {
        return launch(dispatcher + CoroutineName("DockerEvents-$action"), block = block)
    }

    private fun getServerByContainerId(containerId: String): Server? {
        return core.serverManager.getServerList().find {
            it.container.id == containerId
        }
    }

    private suspend fun inspectServerByContainerId(containerId: String) {
        getServerByContainerId(containerId)?.let {
            core.serverManager.inspectServer(it)
        }
    }

    private suspend fun onServerStart(containerId: String, timestamp: Long) {
        val server = getServerByContainerId(containerId) ?: return
        core.eventBus.publish(ServerStartedEvent(server, duration = Duration.ofMillis(Instant.now().toEpochMilli() - timestamp)))
        logger.info("Server ${server.name} started.")
        core.serverManager.inspectServer(server)
    }

    private suspend fun onServerStop(containerId: String, timestamp: Long) {
        val server = getServerByContainerId(containerId) ?: return
        core.eventBus.publish(ServerStoppedEvent(server, duration = Duration.ofMillis(Instant.now().toEpochMilli() - timestamp)))
        logger.info("Server ${server.name} stopped.")
        core.serverManager.inspectServer(server)
    }

    override fun close() {
        callback?.close()
        dispatcher.close()
    }

}