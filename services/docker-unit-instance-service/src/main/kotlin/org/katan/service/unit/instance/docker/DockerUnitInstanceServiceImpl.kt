package org.katan.service.unit.instance.docker

import com.github.dockerjava.api.DockerClient
import com.github.dockerjava.api.command.CreateContainerResponse
import com.github.dockerjava.api.command.PullImageResultCallback
import com.github.dockerjava.api.exception.NotFoundException
import com.github.dockerjava.api.model.PullResponseItem
import com.github.dockerjava.core.DefaultDockerClientConfig
import com.github.dockerjava.core.DockerClientBuilder
import kotlinx.coroutines.CoroutineName
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.suspendCancellableCoroutine
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import kotlinx.coroutines.withContext
import org.apache.logging.log4j.LogManager
import org.katan.config.KatanConfig
import org.katan.event.EventScope
import org.katan.model.unit.UnitInstance
import org.katan.model.unit.UnitInstanceStatus
import org.katan.service.id.IdService
import org.katan.service.unit.instance.UnitInstanceService
import org.katan.service.unit.instance.UnitInstanceSpec
import java.io.Closeable
import kotlin.coroutines.resume
import kotlin.coroutines.suspendCoroutine
import kotlin.reflect.jvm.jvmName

/**
 * This implementation does not directly change the repository because the repository is handled by
 * the Docker events listener.
 */
internal class DockerUnitInstanceServiceImpl(
    private val config: KatanConfig,
    private val idService: IdService,
    private val eventsDispatcher: EventScope
) : UnitInstanceService, CoroutineScope by CoroutineScope(
    CoroutineName(DockerUnitInstanceServiceImpl::class.jvmName)
) {

    private companion object {
        private val logger = LogManager.getLogger(DockerUnitInstanceServiceImpl::class.java)
    }

    init {
        DockerEventScope({ client }, eventsDispatcher, coroutineContext)
    }

    private val client: DockerClient by lazy { initClient() }
    private val mutex = Mutex()
    private val instances: MutableMap<Long, UnitInstance> = hashMapOf()

    override suspend fun getInstance(id: Long): UnitInstance? {
        return mutex.withLock { instances[id] }
    }

    override suspend fun deleteInstance(instance: UnitInstance) {
        withContext(Dispatchers.IO) {
            client.removeContainerCmd(instance.container)
                .withRemoveVolumes(true)
                .withForce(true)
                .exec()
        }
    }

    override suspend fun stopInstance(instance: UnitInstance) {
        if (!instance.status.canBeStopped())
            throw IllegalStateException("Instance cannot be stopped: ${instance.status.name}")

        withContext(Dispatchers.IO) {
            client.stopContainerCmd(instance.container).exec()
        }
    }

    override suspend fun startInstance(instance: UnitInstance) {
        withContext(Dispatchers.IO) {
            client.startContainerCmd(instance.container).exec()
        }
    }

    override suspend fun killInstance(instance: UnitInstance) {
        withContext(Dispatchers.IO) {
            client.killContainerCmd(instance.container).exec()
        }
    }

    override fun fromSpec(data: Map<String, Any>): UnitInstanceSpec {
        check(data.containsKey(IMAGE_PROPERTY)) { "Missing required property \"$IMAGE_PROPERTY\"." }
        return DockerUnitInstanceSpec(data.getValue(IMAGE_PROPERTY) as String)
    }

    override suspend fun createInstanceFor(spec: UnitInstanceSpec): UnitInstance {
        require(spec is DockerUnitInstanceSpec) { "Instance spec must be a DockerUnitInstanceSpec" }

        logger.info("Generating a unit instance: $spec")
        return coroutineScope {
            // TODO better context switch
            val containerId = withContext(Dispatchers.IO) {
                tryCreateContainer(spec.image)
            }

            logger.info("Unit instance generated successfully: $containerId")
            val container =
                withContext(Dispatchers.IO) { client.inspectContainerCmd(containerId).exec() }

            logger.info("Generated instance name: ${container.name}")
//            val connection =
//                networkService.createUnitConnection(container.networkSettings.ipAddress, 8080)

            val id = idService.generate()
            val instance = UnitInstance(
                id,
                spec.image,
                UnitInstanceStatus.None,
                containerId
            )

            mutex.withLock { instances[id] = instance }
            instance
        }
    }

    /**
     * Tries to create a container using the given [image].
     *
     * If the given image is not available, it will pull the image and then try to create the
     * container again suspending the current coroutine for both jobs.
     * @return The created container id.
     */
    private suspend fun tryCreateContainer(image: String): String {
        return try {
            createContainer(image)
        } catch (e: NotFoundException) {
            pullContainerImage(image)
            createContainer(image)
        }
    }

    /**
     * Creates a Docker container using the given [image] suspending the coroutine until the
     * container creation workflow is completed.
     */
    private suspend fun createContainer(image: String): String =
        suspendCoroutine<CreateContainerResponse> { cont ->
            cont.resumeWith(runCatching {
                client.createContainerCmd(image).exec()
            })
        }.id

    /**
     * Pulls a Docker image from suspending the current coroutine until that image pulls completely.
     */
    private suspend fun pullContainerImage(image: String) =
        suspendCancellableCoroutine<Unit> { cont ->
            client.pullImageCmd(image).exec(object : PullImageResultCallback() {
                override fun onStart(stream: Closeable?) {
                    logger.info("Preparing to pull image...")
                }

                override fun onNext(item: PullResponseItem?) {
                    logger.info("Pulling \"$image\"... $item")
                }

                override fun onError(throwable: Throwable?) {
                    cont.cancel(throwable)
                }

                override fun onComplete() {
                    logger.info("Image \"$image\" pull completed")
                    cont.resume(Unit)
                }
            })
        }

    /**
     * Initializes a [DockerClient] with [KatanConfig.DockerClientConfig.host] as Docker host.
     */
    private fun initClient(): DockerClient {
        return DockerClientBuilder.getInstance(
            DefaultDockerClientConfig.createDefaultConfigBuilder()
                .withDockerHost(config.docker.host)
                .build()
        )
            .build()
    }

}