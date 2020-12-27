package me.devnatan.katan.core.docker

import com.github.dockerjava.api.async.ResultCallback
import com.github.dockerjava.api.model.Event
import com.github.dockerjava.api.model.EventType
import kotlinx.coroutines.*
import me.devnatan.katan.api.event.ServerStartedEvent
import me.devnatan.katan.api.event.ServerStoppedEvent
import me.devnatan.katan.api.server.Server
import me.devnatan.katan.core.KatanCore
import java.io.Closeable
import java.time.Duration
import java.time.Instant
import java.util.concurrent.Executors

class DockerEventsListener(private val core: KatanCore): CoroutineScope by CoroutineScope(
    Executors.newSingleThreadExecutor().asCoroutineDispatcher() + CoroutineName("DockerEvents")),
    Closeable {

    private var callback: ResultCallback<Event>? = null

    @OptIn(ExperimentalCoroutinesApi::class)
    fun listen() {
        callback = object: ResultCallback.Adapter<Event>() {
            override fun onNext(event: Event) {
                val container = event.actor!!.id!!
                val action = event.action!!
                KatanCore.logger.debug("[Event: container] $action: $container")

                when (action) {
                    "start" -> launchLocally(action) { onServerStart(container, event.time) }
                    "stop" -> launchLocally(action) { onServerStop(container, event.time) }
                    "pause", "unpause", "kill", "die", "oom" -> launchLocally(action) {
                        inspectServerByContainerId(container)
                    }
                    else -> { /* ignore */ }
                }
            }
        }

        core.docker.eventsCmd().withEventTypeFilter(EventType.CONTAINER).exec(callback)
    }

    fun launchLocally(action: String, block: suspend CoroutineScope.() -> Unit): Job {
        println("Launch locally $action")
        return launch(CoroutineName("DockerEvents-$action"), block = block)
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
        println("onServerStart $containerId (${core.serverManager.getServerList().map { it.container.id }})")
        getServerByContainerId(containerId)?.let { server ->
            println("afterOnServerStart")
            val duration = Duration.between(Instant.ofEpochMilli(timestamp), Instant.now())
            core.eventBus.publish(ServerStartedEvent(server, duration = duration))
            KatanCore.logger.info("Server ${server.name} started in ${String.format("%.2f", duration.toMillis().toFloat())}ms.")

            core.serverManager.inspectServer(server)
        }
    }

    private suspend fun onServerStop(containerId: String, timestamp: Long) {
        println("onServerStop $containerId (${core.serverManager.getServerList().map { it.container.id }})")
        getServerByContainerId(containerId)?.let { server ->
            println("afterOnServerStop")
            val duration = Duration.between(Instant.ofEpochMilli(timestamp), Instant.now())
            core.eventBus.publish(ServerStoppedEvent(server, duration = duration))
            KatanCore.logger.info("Server ${server.name} stopped in ${String.format("%.2f", duration.toMillis().toFloat())}ms.")

            core.serverManager.inspectServer(server)
        }
    }

    override fun close() {
        callback?.close()
        cancel()
    }

}