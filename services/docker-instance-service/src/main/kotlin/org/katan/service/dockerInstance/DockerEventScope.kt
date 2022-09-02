package org.katan.service.dockerInstance

import com.github.dockerjava.api.DockerClient
import com.github.dockerjava.api.async.ResultCallback
import com.github.dockerjava.api.model.Event
import kotlinx.coroutines.CoroutineName
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import org.apache.logging.log4j.LogManager
import org.apache.logging.log4j.Logger
import org.katan.event.EventScope
import java.io.Closeable
import kotlin.coroutines.Continuation
import kotlin.coroutines.CoroutineContext
import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException
import kotlin.coroutines.suspendCoroutine
import kotlin.reflect.jvm.jvmName

// TODO use it
internal class DockerEventScope(
    private val client: DockerClient,
    private val eventsDispatcher: EventScope,
    parentCoroutineContext: CoroutineContext
) : CoroutineScope by CoroutineScope(parentCoroutineContext + CoroutineName(DockerEventScope::class.jvmName)) {

    companion object {
        private const val MAX_LISTEN_RETRY_ATTEMPTS = 3
        private val logger: Logger = LogManager.getLogger(DockerEventScope::class.java)
    }

    init {
        val job = Job()

        // prevent mistakes heheheh :)
        job.invokeOnCompletion {
            throw IllegalStateException("Events listener job cannot be completed")
        }

        launch(job) {
            tryListen()
        }
    }

    /**
     * Listens for Docker events.
     */
    private fun listen(cont: Continuation<Unit>) {
        client.eventsCmd()
            .exec(object : ResultCallback.Adapter<Event>() {
                override fun onStart(stream: Closeable?) {
                    cont.resume(Unit)
                    logger.debug("Docker events listener operation started.")
                }

                override fun onNext(event: Event) {
                    handle(event)
                }

                override fun onError(throwable: Throwable) {
                    cont.resumeWithException(throwable)
                    super.onError(throwable)
                }

                override fun onComplete() {
                    logger.debug("Docker events listener operation completed.")
                }
            })
    }

    /**
     * TODO
     */
    private suspend fun tryListen(currRetryCount: Int = 1) {
        try {
            suspendCoroutine<Unit> { listen(it) }
        } catch (e: Throwable) {
            logger.error(
                "Failed to listen Docker events (%d of %d).".format(
                    currRetryCount,
                    MAX_LISTEN_RETRY_ATTEMPTS
                ),
                e
            )

            if (currRetryCount == MAX_LISTEN_RETRY_ATTEMPTS) {
                return
            }

            tryListen(currRetryCount + 1)
        }
    }

    private fun handle(event: Event) {
        if (event.type == null) return

        launch(Dispatchers.IO) {
            event.type?.let { eventType ->
                logger.debug(event.toString())
                eventsDispatcher.dispatch(DockerEvent(eventType.value))
            }
        }
    }
}

@JvmInline
internal value class DockerEvent(val type: String)
