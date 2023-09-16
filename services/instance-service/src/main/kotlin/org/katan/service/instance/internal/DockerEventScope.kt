package org.katan.service.instance.internal

import kotlinx.coroutines.CoroutineName
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.launch
import me.devnatan.yoki.Yoki
import org.apache.logging.log4j.LogManager
import org.apache.logging.log4j.Logger
import org.katan.EventsDispatcher
import kotlin.coroutines.CoroutineContext
import kotlin.reflect.jvm.jvmName

internal class DockerEventScope(
    private val client: Yoki,
    private val eventsDispatcher: EventsDispatcher,
    coroutineContext: CoroutineContext,
) : CoroutineScope by CoroutineScope(coroutineContext + SupervisorJob() + CoroutineName(DockerEventScope::class.jvmName)) {

    companion object {

        private val logger: Logger = LogManager.getLogger(DockerEventScope::class.java)
    }

    init {
        launch {
            client.system.events().collect { event ->
                logger.debug(event.toString())
                eventsDispatcher.dispatch(DockerEvent(event.type.name.lowercase()))
            }
        }
    }
}

@JvmInline
internal value class DockerEvent(val type: String)
