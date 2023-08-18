package org.katan.service.instance

import kotlinx.coroutines.CoroutineName
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onCompletion
import kotlinx.coroutines.flow.onStart
import me.devnatan.yoki.Yoki
import me.devnatan.yoki.models.exec.ExecCreateOptions
import me.devnatan.yoki.models.exec.ExecStartOptions
import me.devnatan.yoki.models.image.ImagePull
import me.devnatan.yoki.resource.container.create
import me.devnatan.yoki.resource.container.exec
import me.devnatan.yoki.resource.container.remove
import org.apache.logging.log4j.LogManager
import org.katan.config.KatanConfig
import org.katan.event.EventScope
import org.katan.model.Snowflake
import org.katan.model.blueprint.Blueprint
import org.katan.model.blueprint.BlueprintSpecImage
import org.katan.model.instance.InstanceInternalStats
import org.katan.model.instance.InstanceRuntime
import org.katan.model.instance.InstanceStatus
import org.katan.model.instance.InstanceUpdateCode
import org.katan.model.instance.UnitInstance
import org.katan.model.io.HostPort
import org.katan.model.io.NetworkException
import org.katan.model.unit.ImageUpdatePolicy
import org.katan.service.blueprint.BlueprintService
import org.katan.service.id.IdService
import org.katan.service.instance.internal.DockerEventScope
import org.katan.service.instance.repository.InstanceEntity
import org.katan.service.instance.repository.InstanceRepository
import org.katan.service.network.NetworkService
import kotlin.reflect.jvm.jvmName

/**
 * This implementation does not directly change the repository because the repository is handled by
 * the Docker events listener.
 */
internal class DockerInstanceServiceImpl(
    eventsDispatcher: EventScope,
    private val idService: IdService,
    private val networkService: NetworkService,
    private val blueprintService: BlueprintService,
    private val dockerClient: Yoki,
    private val instanceRepository: InstanceRepository,
    private val config: KatanConfig
) : InstanceService,
    CoroutineScope by CoroutineScope(
        SupervisorJob() +
            CoroutineName(DockerInstanceServiceImpl::class.jvmName)
    ) {

    private companion object {
        private val logger = LogManager.getLogger(DockerInstanceServiceImpl::class.java)

        private const val BASE_LABEL = "org.katan.instance."
    }

    init {
        DockerEventScope(dockerClient, eventsDispatcher, coroutineContext)
    }

    override suspend fun getInstance(id: Long): UnitInstance {
        // TODO cache service
        return instanceRepository.findById(id)?.toDomain()
            ?: throw InstanceNotFoundException()
    }

    override suspend fun getInstanceLogs(id: Long): Flow<String> {
        val instance = getInstance(id)
        if (instance.runtime == null) {
            throw InstanceUnreachableRuntimeException()
        }

        return flow { }
        // TODO get container logs
//         TODO handle docker client calls properly
//        return callbackFlow {
//            dockerClient.logContainerCmd(instance.containerId!!)
//                .withStdOut(true)
//                .withFollowStream(true)
//                .withTimestamps(true)
//                .exec(object : ResultCallback.Adapter<Frame>() {
//                    override fun onStart(stream: Closeable?) {
//                        logger.info("started logs streaming")
//                    }
//
//                    override fun onNext(value: Frame) {
//                        trySendBlocking("${value.streamType} ${value.payload.decodeToString()}")
//                            .onFailure {
//                                // TODO handle downstream unavailability properly
//                                logger.error("Downstream closed", it)
//                            }
//                    }
//
//                    override fun onError(error: Throwable) {
//                        cancel(CancellationException("Docker API error", error))
//                    }
//
//                    override fun onComplete() {
//                        logger.info("completed logs streaming")
//                        channel.close()
//                    }
//                })
//
//            awaitClose()
//        }
    }

    override suspend fun runInstanceCommand(id: Long, command: String) {
        val instance = getInstance(id)
        val cid = instance.containerId ?: throw InstanceUnreachableRuntimeException()
        val eid = dockerClient.containers.exec(
            container = cid,
            options = ExecCreateOptions().apply {
                this.command = command.split(" ")
                tty = true
                attachStdin = false
                attachStdout = false
                attachStderr = false
            }
        )

        dockerClient.exec.start(
            id = eid,
            options = ExecStartOptions().apply {
                detach = true
            }
        )
    }

    override suspend fun streamInternalStats(id: Long): Flow<InstanceInternalStats> {
        val instance = getInstance(id)
        val runtime = instance.runtime ?: throw InstanceUnreachableRuntimeException()

        // TODO implement /containers/:id/stats on Yoki
        // TODO handle docker client calls properly
        return flow { }
    }

    override suspend fun deleteInstance(instance: UnitInstance) {
        instanceRepository.delete(instance.id)
        dockerClient.containers.remove(instance.containerId!!) {
            removeAnonymousVolumes = true
            force = true
        }
    }

    private suspend fun startInstance(containerId: String, currentStatus: InstanceStatus) {
        check(!isRunning(currentStatus)) {
            "Unit instance is already running, cannot be started again, stop it first"
        }

        dockerClient.containers.start(containerId)
    }

    private suspend fun stopInstance(containerId: String, currentStatus: InstanceStatus) {
        check(isRunning(currentStatus)) {
            "Unit instance is not running, cannot be stopped"
        }

        dockerClient.containers.stop(containerId)
    }

    private suspend fun killInstance(containerId: String) {
        dockerClient.containers.kill(containerId)
    }

    private suspend fun restartInstance(instance: UnitInstance) {
        val cid = instance.containerId ?: throw InstanceUnreachableRuntimeException()

        // container will be deleted so restart command will fail
        if (tryUpdateImage(cid, instance.updatePolicy)) {
            return
        }

        dockerClient.containers.restart(cid)
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

        val currImage = dockerClient.containers.inspect(containerId).image

        // fast path -- version-specific tag
        if (currImage.substringAfterLast(":") == "latest") {
            return false
        }

        logger.debug("Removing old image \"$currImage\"...")
        dockerClient.containers.remove(currImage)

        pullContainerImage(currImage).collect {
            logger.debug("Pulling image... $it")
        }
        return true
    }

    private suspend fun updateInstance(id: Long, status: InstanceStatus) {
        instanceRepository.update(id) {
            this.status = status.value
        }
    }

    // TODO check for parameters invalid property types
    override suspend fun updateInstanceStatus(
        instance: UnitInstance,
        code: InstanceUpdateCode
    ) {
        val containerId = requireNotNull(instance.containerId) {
            "Cannot update non-initialized instance container"
        }

        when (code) {
            InstanceUpdateCode.Start -> startInstance(
                containerId,
                instance.status
            )

            InstanceUpdateCode.Stop -> stopInstance(containerId, instance.status)
            InstanceUpdateCode.Restart -> restartInstance(instance)
            InstanceUpdateCode.Kill -> killInstance(containerId)
        }
    }

    override suspend fun createInstance(blueprint: Blueprint, host: String?, port: Int?): UnitInstance {
        val instanceId = idService.generate()
        val spec = blueprintService.getSpec(blueprint.id)
        val generatedName = generateContainerName(instanceId, spec.build?.instance?.name)
        val image = (spec.build?.image as BlueprintSpecImage.Identifier).id

        // we'll try to create the container using the given image if the image is not available,
        // it will pull the image and then try to create the container again
        val cid = createContainer(instanceId, image, generatedName)
        return try {
            resumeCreateInstance(
                instanceId = instanceId,
                blueprintId = blueprint.id,
                containerId = cid,
                host = host,
                port = port,
                status = InstanceStatus.Created
            )
        } catch (e: InstanceNotFoundException) {
            var status: InstanceStatus = InstanceStatus.ImagePullNeeded
            val instance = registerInstance(instanceId, blueprint.id, status)

            pullImageAndUpdateInstance(instanceId, image) { status = it }

            val containerId = createContainer(instanceId, image, generatedName)
            resumeCreateInstance(
                instanceId = instanceId,
                blueprintId = blueprint.id,
                containerId = containerId,
                host = host,
                port = port,
                status = status,
                fallbackInstance = instance
            )
        }
    }

    private suspend fun resumeCreateInstance(
        instanceId: Long,
        blueprintId: Snowflake,
        containerId: String,
        host: String?,
        port: Int?,
        status: InstanceStatus,
        fallbackInstance: UnitInstance? = null
    ): UnitInstance {
        var finalStatus: InstanceStatus = status
        logger.debug("Connecting $instanceId to ${config.dockerNetwork}...")

        val connection = try {
            networkService.connect(
                network = config.dockerNetwork,
                instance = containerId,
                host = host,
                port = port?.toShort()
            )
        } catch (e: NetworkException) {
            finalStatus = InstanceStatus.NetworkAssignmentFailed
            fallbackInstance?.let { updateInstance(it.id, finalStatus) }
            logger.error("Unable to connect the instance ($instanceId) to the network.", e)
            null
        }

        logger.debug("Connected {} to {} @ {}", instanceId, config.dockerNetwork, connection)

        // fallback instance can set if instance was not created asynchronously
        if (fallbackInstance == null) {
            return registerInstance(
                instanceId = instanceId,
                blueprintId = blueprintId,
                status = finalStatus,
                containerId = containerId,
                connection = connection
            )
        }

        return fallbackInstance
    }

    private suspend fun registerInstance(
        instanceId: Long,
        blueprintId: Snowflake,
        status: InstanceStatus,
        containerId: String? = null,
        connection: HostPort? = null
    ): UnitInstance {
        val instance = DockerUnitInstanceImpl(
            id = instanceId,
            status = status,
            updatePolicy = ImageUpdatePolicy.Always,
            containerId = containerId,
            connection = connection,
            runtime = containerId?.let { buildRuntime(it) },
            blueprintId = blueprintId
        )

        instanceRepository.create(instance)
        return instance
    }

    private suspend fun pullImageAndUpdateInstance(
        instanceId: Long,
        image: String,
        onUpdate: (InstanceStatus) -> Unit
    ) = pullContainerImage(image).onStart {
        with(InstanceStatus.ImagePullInProgress) {
            onUpdate(this)
            updateInstance(instanceId, this)
        }
        logger.debug("Image pull started")
    }.onCompletion { error ->
        val status =
            if (error == null) InstanceStatus.ImagePullCompleted else InstanceStatus.ImagePullFailed

        if (error != null) {
            logger.error("Failed to pull image.", error)
            error.printStackTrace()
        }

        onUpdate(status)
        updateInstance(instanceId, status)
        logger.debug("Image pull completed")
    }.collect {
        logger.debug("Pulling ($image): $it")
    }

    private fun generateContainerName(id: Long, name: String?): String {
        if (name != null) {
            return name.replace("{id}", id.toString())
        }

        return buildString {
            append("katan")
            append("-${config.nodeId}-")
            append(id)
        }
    }

    /**
     * Creates a Docker container using the given [image] suspending the coroutine until the
     * container creation workflow is completed.
     */
    private suspend fun createContainer(instanceId: Long, image: String, name: String): String {
        logger.debug("Creating container with ($image) to $instanceId...")
        return dockerClient.containers.create {
            this.image = image
            this.name = name
            // TODO force tty to false
//            this.tt
            labels = createDefaultContainerLabels(instanceId)
        }
    }

    private fun createDefaultContainerLabels(instanceId: Long): Map<String, String> =
        mapOf("id" to BASE_LABEL + instanceId)

    /**
     * Pulls a Docker image from suspending the current coroutine until that image pulls completely.
     */
    private suspend fun pullContainerImage(image: String): Flow<String> =
        dockerClient.images.pull(image).map(ImagePull::toString)

    private suspend fun buildRuntime(containerId: String): InstanceRuntime? {
        val inspection = dockerClient.containers.inspect(containerId)
//        val networkSettings = inspection.networkSettings
        val state = inspection.state

        return InstanceRuntimeImpl(
            id = inspection.id,
            network = InstanceRuntimeNetworkImpl(
                ipV4Address = "",
                hostname = null,
                networks = emptyList()
                // TODO missing properties
//                ipV4Address = networkSettings.ipAddress,
//                hostname = inspection.config.hostName,
//                networks = networkSettings.networks.map { (name, settings) ->
//                    InstanceRuntimeSingleNetworkImpl(
//                        id = settings.networkID ?: "",
//                        name = name,
//                        ipv4Address = settings.ipamConfig?.ipv4Address?.ifBlank { null },
//                        ipv6Address = settings.ipamConfig?.ipv6Address?.ifBlank { null }
//                    )
//                }
            ),
            platform = inspection.platform.ifBlank { null },
            exitCode = state.exitCode ?: 0,
            pid = state.pid ?: 0,
            startedAt = state.startedAt,
            finishedAt = state.finishedAt,
            error = state.error?.ifBlank { null },
            status = state.status.value,
            fsPath = null,
            // TODO missing properties
//            fsPath = inspection.config.keys.firstOrNull(),
            outOfMemory = state.oomKilled,
            mounts = emptyList()
//            mounts = inspection.mouts?.map { mount ->
//                InstanceRuntimeMountImpl(
//                    type = (mount.rawValues["Type"] as? String) ?: "volume",
//                    target = mount.name.orEmpty(),
//                    destination = mount.destination?.path.orEmpty(),
//                    readonly = !(mount.rw ?: false)
//                )
//            }.orEmpty()
        )
    }

    private fun isRunning(status: InstanceStatus): Boolean {
        return status == InstanceStatus.Running ||
            status == InstanceStatus.Restarting ||
            status == InstanceStatus.Stopping ||
            status == InstanceStatus.Paused
    }

    private suspend fun InstanceEntity.toDomain(): UnitInstance {
        return DockerUnitInstanceImpl(
            id = getId(),
            updatePolicy = ImageUpdatePolicy.getById(updatePolicy),
            containerId = containerId,
            status = toStatus(status),
            connection = networkService.createConnection(host, port),
            runtime = containerId?.let { buildRuntime(it) },
            blueprintId = blueprintId
        )
    }

    private fun toStatus(value: String): InstanceStatus {
        return when (value.lowercase()) {
            "created" -> InstanceStatus.Created
            "network-assignment-failed" -> InstanceStatus.NetworkAssignmentFailed
            "unavailable" -> InstanceStatus.Unavailable
            "image-pull" -> InstanceStatus.ImagePullInProgress
            "image-pull-needed" -> InstanceStatus.ImagePullNeeded
            "image-pull-failed" -> InstanceStatus.ImagePullFailed
            "image-pull-completed" -> InstanceStatus.ImagePullCompleted
            "dead" -> InstanceStatus.Dead
            "paused" -> InstanceStatus.Paused
            "exited" -> InstanceStatus.Running
            "stopped" -> InstanceStatus.Stopping
            "starting" -> InstanceStatus.Removing
            "removing" -> InstanceStatus.Stopping
            "restarting" -> InstanceStatus.Restarting
            else -> InstanceStatus.Unknown
        }
    }

//    private fun toInternalStats(statistics: Statistics): InstanceInternalStats? {
//        val pid = statistics.pidsStats.current ?: return null
//        val mem = statistics.memoryStats!!
//        val cpu = statistics.cpuStats!!
//        val last = statistics.preCpuStats
//
//        return InstanceInternalStatsImpl(
//            pid = pid,
//            memoryUsage = mem.usage!!,
//            memoryMaxUsage = mem.maxUsage!!,
//            memoryLimit = mem.limit!!,
//            memoryCache = mem.stats!!.cache!!,
//            cpuUsage = cpu.cpuUsage!!.totalUsage!!,
//            perCpuUsage = cpu.cpuUsage!!.percpuUsage!!.toLongArray(),
//            systemCpuUsage = cpu.systemCpuUsage!!,
//            onlineCpus = cpu.onlineCpus!!,
//            lastCpuUsage = last.cpuUsage?.totalUsage,
//            lastPerCpuUsage = last.cpuUsage?.percpuUsage?.toLongArray(),
//            lastSystemCpuUsage = last.systemCpuUsage,
//            lastOnlineCpus = last.onlineCpus
//        )
//    }
}
