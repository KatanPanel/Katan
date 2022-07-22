package org.katan.service.unit.instance.docker

import com.github.dockerjava.api.DockerClient
import com.github.dockerjava.api.async.ResultCallback
import com.github.dockerjava.api.model.Event
import kotlinx.coroutines.CoroutineName
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import org.katan.event.EventScope
import kotlin.coroutines.CoroutineContext
import kotlin.reflect.jvm.jvmName

internal class EventsListener(
    private inline val client: () -> DockerClient,
    private val eventsDispatcher: EventScope,
    parentCoroutineContext: CoroutineContext
) : CoroutineScope by CoroutineScope(parentCoroutineContext + CoroutineName(EventsListener::class.jvmName)) {

    init {
        val job = Job()

        // prevent mistakes heheheh :)
        job.invokeOnCompletion {
            throw IllegalStateException("Events listener job cannot be completed")
        }

        launch(job) { listen() }
    }

    private fun listen() {
        client().eventsCmd()
            .exec(object : ResultCallback.Adapter<Event>() {
                override fun onNext(event: Event) {
                    handle(event)
                }
            })
    }

    private fun handle(event: Event) {
        if (event.type == null) return

        launch(Dispatchers.IO) {
            event.type?.let { eventsDispatcher.dispatch(DockerEvent(it.value)) }
        }
    }

}

@JvmInline
private value class DockerEvent(val type: String) : org.katan.event.Event