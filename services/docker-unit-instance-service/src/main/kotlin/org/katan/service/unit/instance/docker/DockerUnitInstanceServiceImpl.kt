package org.katan.service.unit.instance.docker

import com.github.dockerjava.api.DockerClient
import com.github.dockerjava.api.command.CreateContainerCmd
import com.github.dockerjava.api.command.CreateContainerResponse
import com.github.dockerjava.api.exception.NotFoundException
import com.github.dockerjava.api.model.PullResponseItem
import com.github.dockerjava.core.DefaultDockerClientConfig
import com.github.dockerjava.core.DockerClientBuilder
import kotlinx.coroutines.CoroutineName
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.withContext
import org.apache.logging.log4j.LogManager
import org.katan.config.KatanConfig
import org.katan.event.EventScope
import org.katan.model.instance.UnitInstance
import org.katan.model.instance.UnitInstanceStatus
import org.katan.model.instance.UnitInstanceUpdateStatusCode
import org.katan.model.unit.ImageUpdatePolicy
import org.katan.service.id.IdService
import org.katan.service.unit.instance.InstanceNotFoundException
import org.katan.service.unit.instance.UnitInstanceService
import org.katan.service.unit.instance.UnitInstanceSpec
import org.katan.service.unit.instance.docker.model.DockerUnitInstanceImpl
import org.katan.service.unit.instance.docker.util.attachResultCallback
import org.katan.service.unit.instance.repository.UnitInstanceRepository
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

    override suspend fun getInstance(id: Long): UnitInstance {
        // TODO cache service
        return unitInstanceRepository.findById(id)
            ?: throw InstanceNotFoundException()
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

    private suspend fun startInstance(containerId: String, currentStatus: UnitInstanceStatus) {
        check(!isRunning(currentStatus)) {
            "Unit instance is already running, cannot be started again, stop it first"
        }

        withContext(Dispatchers.IO) {
            dockerClient.startContainerCmd(containerId).exec()
        }
    }

    private suspend fun stopInstance(containerId: String, currentStatus: UnitInstanceStatus) {
        check(isRunning(currentStatus)) {
            "Unit instance is not running, cannot be stopped"
        }

        withContext(Dispatchers.IO) {
            dockerClient.stopContainerCmd(containerId).exec()
        }
    }

    private suspend fun killInstance(containerId: String) {
        withContext(Dispatchers.IO) {
            dockerClient.killContainerCmd(containerId).exec()
        }
    }

    private suspend fun restartInstance(instance: UnitInstance) {
        // container will be deleted so restart command will fail
        if (tryUpdateImage(instance.containerId, instance.imageUpdatePolicy)) {
            return
        }

        withContext(Dispatchers.IO) {
            dockerClient.restartContainerCmd(instance.containerId).exec()
        }
    }

    private suspend fun tryUpdateImage(
        containerId: String,
        imageUpdatePolicy: ImageUpdatePolicy
    ): Boolean {
        // fast path -- ignore image update if policy is set to Never
        if (imageUpdatePolicy == ImageUpdatePolicy.Never) {
            return false
        }

        logger.debug("Trying to update container image")

        val inspect = withContext(Dispatchers.IO) {
            dockerClient.inspectContainerCmd(containerId).exec()
        } ?: throw RuntimeException("Failed to inspect container: $containerId")

        val currImage = inspect.config.image ?: return false

        // fast path -- version-specific tag
        if (currImage.substringAfterLast(":") == "latest") {
            return false
        }

        logger.debug("Removing image \"$currImage\"...")
        withContext(Dispatchers.IO) {
            dockerClient.removeImageCmd(currImage).exec()
        }

        pullContainerImage(currImage).collect {
            logger.info("Pulling image... $it")
        }
        return true
    }

    // TODO check for parameters invalid property types
    override suspend fun updateInstanceStatus(
        instance: UnitInstance,
        code: UnitInstanceUpdateStatusCode
    ) {
        when (code) {
            UnitInstanceUpdateStatusCode.Start -> startInstance(
                instance.containerId,
                instance.status
            )

            UnitInstanceUpdateStatusCode.Stop -> stopInstance(instance.containerId, instance.status)
            UnitInstanceUpdateStatusCode.Restart -> restartInstance(instance)
            UnitInstanceUpdateStatusCode.Kill -> killInstance(instance.containerId)
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
            val instanceId = idService.generate()

            // TODO better context switch
            val generatedContainerId = withContext(Dispatchers.IO) {
                tryCreateContainer(spec.image, "katan-$instanceId")
            }

            logger.info("Unit instance generated successfully: $generatedContainerId")
            val createdContainer = withContext(Dispatchers.IO) {
                dockerClient.inspectContainerCmd(generatedContainerId).exec()
            }

            logger.info("Generated instance name: ${createdContainer.name}")
//            val connection =
//                networkService.createUnitConnection(container.networkSettings.ipAddress, 8080)

            val instance = DockerUnitInstanceImpl(
                id = instanceId,
                status = UnitInstanceStatus.Created,
                imageUpdatePolicy = ImageUpdatePolicy.Always,
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
    private suspend fun tryCreateContainer(image: String, name: String): String {
        return try {
            createContainer(image, name)
        } catch (e: NotFoundException) {
            pullContainerImage(image).collect()
            createContainer(image, name)
        }
    }

    /**
     * Creates a Docker container using the given [image] suspending the coroutine until the
     * container creation workflow is completed.
     */
    private suspend fun createContainer(image: String, name: String): String =
        suspendCoroutine<CreateContainerResponse> { cont ->
            cont.resumeWith(
                runCatching {
                    dockerClient.createContainerCmd(image)
                        .withName(name)
                        .withEnv(mapOf("EULA" to "true").map { (k, v) -> "$k=$v" })
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
    private suspend fun pullContainerImage(image: String): Flow<String> {
        return callbackFlow {
            dockerClient.pullImageCmd(image).exec(
                attachResultCallback<PullResponseItem, String> {
                    it.toString()
                }
            )

            awaitClose()
        }
    }

    private fun isRunning(status: UnitInstanceStatus): Boolean {
        return false
    }
}
