package org.katan.service.unit.instance.docker

import com.github.dockerjava.api.DockerClient
import com.github.dockerjava.api.command.CreateContainerCmd
import com.github.dockerjava.api.command.CreateContainerResponse
import com.github.dockerjava.api.command.PullImageResultCallback
import com.github.dockerjava.api.exception.NotFoundException
import com.github.dockerjava.api.model.PullResponseItem
import com.github.dockerjava.core.DefaultDockerClientConfig
import com.github.dockerjava.core.DockerClientBuilder
import kotlinx.coroutines.CoroutineName
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.suspendCancellableCoroutine
import kotlinx.coroutines.withContext
import org.apache.logging.log4j.LogManager
import org.katan.config.KatanConfig
import org.katan.event.EventScope
import org.katan.model.unit.UnitInstance
import org.katan.model.unit.UnitInstanceStatus
import org.katan.service.id.IdService
import org.katan.service.unit.instance.UnitInstanceService
import org.katan.service.unit.instance.UnitInstanceSpec
import org.katan.service.unit.instance.docker.model.DockerUnitInstanceImpl
import org.katan.service.unit.instance.repository.UnitInstanceRepository
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
    private val eventsDispatcher: EventScope,
    private val unitInstanceRepository: UnitInstanceRepository
) : UnitInstanceService,
    CoroutineScope by CoroutineScope(
        SupervisorJob() +
            CoroutineName(DockerUnitInstanceServiceImpl::class.jvmName)
    ) {

    private companion object {
        private val logger = LogManager.getLogger(DockerUnitInstanceServiceImpl::class.java)
    }

    private val dockerClient: DockerClient by lazy { initClient() }

    init {
        DockerEventScope({ dockerClient }, eventsDispatcher, coroutineContext)
    }

    override suspend fun getInstance(id: Long): UnitInstance? {
        // TODO cache service
        return unitInstanceRepository.findById(id)
    }

    override suspend fun deleteInstance(instance: UnitInstance) {
        require(instance is DockerUnitInstanceImpl)

        // TODO fix coroutine scope of both runtime remove and repository delete
        unitInstanceRepository.delete(instance.id)
        withContext(Dispatchers.IO) {
            dockerClient.removeContainerCmd(instance.containerId)
                .withRemoveVolumes(true)
                .withForce(true)
                .exec()
        }
    }

    override suspend fun stopInstance(instance: UnitInstance) {
        check(isRunning(instance.status)) {
            "Unit instance is not running, cannot be stopped"
        }

        require(instance is DockerUnitInstanceImpl)
        withContext(Dispatchers.IO) {
            dockerClient.stopContainerCmd(instance.containerId).exec()
        }
    }

    override suspend fun startInstance(instance: UnitInstance) {
        check(!isRunning(instance.status)) {
            "Unit instance is already running, cannot be started again, stop it first"
        }

        require(instance is DockerUnitInstanceImpl)
        withContext(Dispatchers.IO) {
            dockerClient.startContainerCmd(instance.containerId).exec()
        }
    }

    override suspend fun killInstance(instance: UnitInstance) {
        require(instance is DockerUnitInstanceImpl)
        withContext(Dispatchers.IO) {
            dockerClient.killContainerCmd(instance.containerId).exec()
        }
    }

    override suspend fun restartInstance(instance: UnitInstance) {
        require(instance is DockerUnitInstanceImpl)
        withContext(Dispatchers.IO) {
            dockerClient.restartContainerCmd(instance.containerId).exec()
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
            val generatedContainerId = withContext(Dispatchers.IO) {
                tryCreateContainer(spec.image)
            }

            logger.info("Unit instance generated successfully: $generatedContainerId")
            val createdContainer = withContext(Dispatchers.IO) {
                dockerClient.inspectContainerCmd(generatedContainerId).exec()
            }

            logger.info("Generated instance name: ${createdContainer.name}")
//            val connection =
//                networkService.createUnitConnection(container.networkSettings.ipAddress, 8080)

            val instanceId = idService.generate()
            val instance = DockerUnitInstanceImpl(
                id = instanceId,
                status = UnitInstanceStatus.Created,
                imageId = createdContainer.imageId,
                containerId = generatedContainerId
            )

            // TODO cache service
            unitInstanceRepository.create(instance)
            instance
        }
    }

    /**
     * Initializes a [DockerClient] with [KatanConfig.DockerClientConfig.host] as Docker host.
     */
    private fun initClient(): DockerClient {
        return DockerClientBuilder.getInstance(
            DefaultDockerClientConfig.createDefaultConfigBuilder()
                .withDockerHost(config.docker.host)
                .build()
        ).build()
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
            cont.resumeWith(
                runCatching {
                    dockerClient.createContainerCmd(image)
                        .buildContainerBasedOnConfiguration()
                        .exec()
                }
            )
        }.id

    /**
     * Builds container configuration based on Katan settings.
     */
    private fun CreateContainerCmd.buildContainerBasedOnConfiguration() = apply {
//        if (config.docker.network.driver == NETWORK_DRIVER_MACVLAN)
//            applyMacvlanIpAddress()
    }

    /**
     * Pulls a Docker image from suspending the current coroutine until that image pulls completely.
     */
    private suspend fun pullContainerImage(image: String) =
        suspendCancellableCoroutine<Unit> { cont ->
            dockerClient.pullImageCmd(image).exec(object : PullImageResultCallback() {
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

    private fun isRunning(status: UnitInstanceStatus): Boolean {
        return true
    }
}
