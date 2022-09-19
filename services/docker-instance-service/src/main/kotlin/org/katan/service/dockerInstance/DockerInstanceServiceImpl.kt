package org.katan.service.dockerInstance

import com.github.dockerjava.api.DockerClient
import com.github.dockerjava.api.async.ResultCallback
import com.github.dockerjava.api.exception.NotFoundException
import com.github.dockerjava.api.model.Frame
import com.github.dockerjava.api.model.PullResponseItem
import com.github.dockerjava.api.model.Statistics
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.CoroutineName
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers.IO
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.cancel
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.channels.onFailure
import kotlinx.coroutines.channels.trySendBlocking
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.flow.onCompletion
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.withContext
import kotlinx.datetime.Instant
import org.apache.logging.log4j.LogManager
import org.katan.config.KatanConfig
import org.katan.event.EventScope
import org.katan.model.blueprint.Blueprint
import org.katan.model.instance.InstanceInternalStats
import org.katan.model.instance.InstanceRuntime
import org.katan.model.instance.InstanceStatus
import org.katan.model.instance.InstanceUpdateCode
import org.katan.model.instance.UnitInstance
import org.katan.model.net.Connection
import org.katan.model.net.NetworkException
import org.katan.model.unit.ImageUpdatePolicy
import org.katan.service.dockerInstance.model.DockerUnitInstanceImpl
import org.katan.service.dockerInstance.model.InstanceInternalStatsImpl
import org.katan.service.dockerInstance.model.InstanceRuntimeImpl
import org.katan.service.dockerInstance.model.InstanceRuntimeMountImpl
import org.katan.service.dockerInstance.model.InstanceRuntimeNetworkImpl
import org.katan.service.dockerInstance.model.InstanceRuntimeSingleNetworkImpl
import org.katan.service.id.IdService
import org.katan.service.instance.InstanceNotFoundException
import org.katan.service.instance.InstanceService
import org.katan.service.instance.InstanceUnreachableRuntimeException
import org.katan.service.instance.repository.InstanceEntity
import org.katan.service.instance.repository.UnitInstanceRepository
import org.katan.service.network.NetworkService
import java.io.Closeable
import kotlin.reflect.jvm.jvmName

/**
 * This implementation does not directly change the repository because the repository is handled by
 * the Docker events listener.
 */
internal class DockerInstanceServiceImpl(
    private val idService: IdService,
    private val networkService: NetworkService,
    private val dockerClient: DockerClient,
    private val unitInstanceRepository: UnitInstanceRepository,
    private val config: KatanConfig,
    eventsDispatcher: EventScope
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
        return unitInstanceRepository.findById(id)?.toDomain()
            ?: throw InstanceNotFoundException()
    }

    override suspend fun getInstanceLogs(id: Long): Flow<String> {
        val instance = getInstance(id)

        if (instance.runtime == null) {
            throw InstanceUnreachableRuntimeException()
        }

        logger.info("fetch instance lolgs @ ${instance.containerId}")
        // TODO handle docker client calls properly
        return callbackFlow {
            dockerClient.logContainerCmd(instance.containerId!!)
                .withStdOut(true)
                .withFollowStream(true)
                .withTimestamps(true)
                .exec(object : ResultCallback.Adapter<Frame>() {
                    override fun onStart(stream: Closeable?) {
                        logger.info("started logs streaming")
                    }

                    override fun onNext(value: Frame) {
                        trySendBlocking("${value.streamType} ${value.payload.decodeToString()}")
                            .onFailure {
                                // TODO handle downstream unavailability properly
                                logger.error("Downstream closed", it)
                            }
                    }

                    override fun onError(error: Throwable) {
                        cancel(CancellationException("Docker API error", error))
                    }

                    override fun onComplete() {
                        logger.info("completed logs streaming")
                        channel.close()
                    }
                })

            awaitClose()
        }
    }

    override suspend fun runInstanceCommand(id: Long, command: String) {
        val instance = getInstance(id)

        if (instance.containerId == null) {
            throw InstanceUnreachableRuntimeException()
        }

        val cmd = command.split(" ")

        // TODO handle docker client calls properly
        val execId = withContext(IO) {
            dockerClient.execCreateCmd(instance.containerId!!)
                .withCmd(*cmd.toTypedArray())
                .withTty(false)
                .withAttachStdin(false)
                .withAttachStdout(false)
                .withAttachStderr(false)
                .withTty(true)
                .exec()
                .id
        }

        dockerClient.execStartCmd(execId).withDetach(true)
            .exec(object : ResultCallback.Adapter<Frame>() {})
    }

    override suspend fun streamInternalStats(id: Long): Flow<InstanceInternalStats> {
        val instance = getInstance(id)
        val runtime = instance.runtime ?: throw InstanceUnreachableRuntimeException()

        // TODO handle docker client calls properly
        return callbackFlow {
            dockerClient.statsCmd(runtime.id).withNoStream(false)
                .exec(object : ResultCallback.Adapter<Statistics>() {
                    override fun onStart(stream: Closeable?) {
                        logger.info("started stats streaming")
                    }

                    override fun onNext(value: Statistics) {
                        val result = toInternalStats(value) ?: return
                        trySendBlocking(result)
                            .onFailure {
                                // TODO handle downstream unavailability properly
                                logger.error("Downstream closed", it)
                            }
                    }

                    override fun onError(error: Throwable) {
                        error.printStackTrace()
                        cancel(CancellationException("Docker API error", error))
                    }

                    override fun onComplete() {
                        logger.info("completed stats streaming")
                        channel.close()
                    }
                })

            awaitClose()
        }
    }

    override suspend fun deleteInstance(instance: UnitInstance) {
        require(instance is DockerUnitInstanceImpl)

        // TODO fix coroutine scope of both runtime remove and repository delete
        unitInstanceRepository.delete(instance.id)
        withContext(IO) {
            dockerClient.removeContainerCmd(instance.containerId!!)
                .withRemoveVolumes(true)
                .withForce(true)
                .exec()
        }
    }

    private suspend fun startInstance(containerId: String, currentStatus: InstanceStatus) {
        check(!isRunning(currentStatus)) {
            "Unit instance is already running, cannot be started again, stop it first"
        }

        withContext(IO) {
            dockerClient.startContainerCmd(containerId).exec()
        }
    }

    private suspend fun stopInstance(containerId: String, currentStatus: InstanceStatus) {
        check(isRunning(currentStatus)) {
            "Unit instance is not running, cannot be stopped"
        }

        withContext(IO) {
            dockerClient.stopContainerCmd(containerId).exec()
        }
    }

    private suspend fun killInstance(containerId: String) {
        withContext(IO) {
            dockerClient.killContainerCmd(containerId).exec()
        }
    }

    private suspend fun restartInstance(instance: UnitInstance) {
        // container will be deleted so restart command will fail
        if (tryUpdateImage(instance.containerId!!, instance.updatePolicy)) {
            return
        }

        withContext(IO) {
            dockerClient.restartContainerCmd(instance.containerId!!).exec()
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

        val inspect = withContext(IO) {
            dockerClient.inspectContainerCmd(containerId).exec()
        } ?: throw RuntimeException("Failed to inspect container: $containerId")

        val currImage = inspect.config.image ?: return false

        // fast path -- version-specific tag
        if (currImage.substringAfterLast(":") == "latest") {
            return false
        }

        logger.debug("Removing old image \"$currImage\"...")
        withContext(IO) {
            dockerClient.removeImageCmd(currImage).exec()
        }

        pullContainerImage(currImage).collect {
            logger.debug("Pulling image... $it")
        }
        return true
    }

    private suspend fun updateInstance(id: Long, status: InstanceStatus) {
        unitInstanceRepository.update(id) {
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

    override suspend fun createInstance(
        blueprint: Blueprint,
        host: String?,
        port: Int?
    ): UnitInstance {
        val instanceId = idService.generate()
        val raw = blueprint.raw ?: error("Missing raw blueprint for ${blueprint.id}")
        val generatedName = generateContainerName(instanceId, raw.instance?.name)
        val image = raw.build.image

        // we'll try to create the container using the given image if the image is not available,
        // it will pull the image and then try to create the container again
        return try {
            val containerId = createContainer(instanceId, image, generatedName, blueprint)
            resumeCreateInstance(
                instanceId,
                blueprint.id,
                containerId,
                host,
                port,
                InstanceStatus.Created
            )
        } catch (e: NotFoundException) {
            var status: InstanceStatus = InstanceStatus.ImagePullNeeded
            val instance = registerInstance(instanceId, blueprint.id, status)

            logger.debug("Preparing to pull image")
            pullImageAndUpdateInstance(instanceId, image) { status = it }
            logger.debug("Image pulled!")

            val containerId = createContainer(instanceId, image, generatedName, blueprint)
            resumeCreateInstance(
                instanceId,
                blueprint.id,
                containerId,
                host,
                port,
                status,
                instance
            )

            instance
        }
    }

    private suspend fun resumeCreateInstance(
        instanceId: Long,
        blueprintId: Long,
        containerId: String,
        host: String?,
        port: Int?,
        status: InstanceStatus,
        fallbackInstance: UnitInstance? = null
    ): UnitInstance {
        var finalStatus: InstanceStatus = status
        logger.info("Connecting $instanceId to ${config.docker.network.name}...")

        val connection = try {
            networkService.connect(
                config.docker.network.name,
                config.docker.network.driver,
                containerId,
                host,
                port
            )
        } catch (e: NetworkException) {
            finalStatus = InstanceStatus.NetworkAssignmentFailed
            fallbackInstance?.let { updateInstance(it.id, finalStatus) }
            logger.error("Unable to connect the instance ($instanceId) to the network.", e)
            null
        }

        logger.info("Connected $instanceId to ${config.docker.network.name} @ $connection")

        // fallback instance can set if instance was not created asynchronously
        if (fallbackInstance == null) {
            return registerInstance(instanceId, blueprintId, finalStatus, containerId, connection)
        }

        return fallbackInstance
    }

    private suspend fun registerInstance(
        instanceId: Long,
        blueprintId: Long,
        status: InstanceStatus,
        containerId: String? = null,
        connection: Connection? = null
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

        unitInstanceRepository.create(instance)
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
        logger.info("Image pull started")
    }.onCompletion { error ->
        val status =
            if (error == null) InstanceStatus.ImagePullCompleted else InstanceStatus.ImagePullFailed

        if (error != null) {
            logger.error("Failed to pull image.", error)
            error.printStackTrace()
        }

        onUpdate(status)
        updateInstance(instanceId, status)
        logger.info("Image pull completed")
    }.collect {
        logger.info("Pulling ($image): $it")
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
    private fun createContainer(
        instanceId: Long,
        image: String,
        name: String,
        blueprint: Blueprint
    ): String {
        logger.info("Creating container with ($image) to $instanceId...")
        return dockerClient.createContainerCmd(image)
            .withName(name)
            .withTty(false)
            .withEnv(mapOf("EULA" to "true").map { (k, v) -> "$k=$v" })
            .withLabels(createDefaultContainerLabels(instanceId, blueprint))
            .exec().id
    }

    private fun createDefaultContainerLabels(
        instanceId: Long,
        blueprint: Blueprint
    ): Map<String, String> {
        return mapOf(
            "id" to instanceId.toString(),
            "blueprint.name" to blueprint.name,
            "blueprint.version" to blueprint.version,
            "blueprint.type" to blueprint.raw?.type.orEmpty(),
            "blueprint.remote.main" to blueprint.raw?.remote?.main.orEmpty()
        ).mapKeys { key -> BASE_LABEL + key }
    }

    /**
     * Pulls a Docker image from suspending the current coroutine until that image pulls completely.
     */
    private suspend fun pullContainerImage(image: String): Flow<String> {
        return callbackFlow {
            dockerClient.pullImageCmd(image)
                .exec(object : ResultCallback.Adapter<PullResponseItem>() {
                    override fun onNext(value: PullResponseItem) {
                        trySendBlocking(value.toString())
                            .onFailure {
                                // TODO handle downstream unavailability properly
                                logger.error("Downstream closed", it)
                            }
                    }

                    override fun onError(error: Throwable) {
                        cancel(CancellationException("Docker API error", error))
                    }

                    override fun onComplete() {
                        channel.close()
                    }
                })

            awaitClose()
        }
    }

    private suspend fun buildRuntime(containerId: String): InstanceRuntime? {
        val inspect = withContext(IO) {
            dockerClient.inspectContainerCmd(containerId).exec()
        } ?: return null

        val networkSettings = inspect.networkSettings
        val state = inspect.state

        return InstanceRuntimeImpl(
            id = inspect.id,
            network = InstanceRuntimeNetworkImpl(
                ipV4Address = networkSettings.ipAddress,
                hostname = inspect.config.hostName,
                networks = networkSettings.networks.map { (name, settings) ->
                    InstanceRuntimeSingleNetworkImpl(
                        id = settings.networkID ?: "",
                        name = name,
                        ipv4Address = settings.ipamConfig?.ipv4Address?.ifBlank { null },
                        ipv6Address = settings.ipamConfig?.ipv6Address?.ifBlank { null }
                    )
                }
            ),
            platform = inspect.platform?.ifBlank { null },
            exitCode = state.exitCodeLong ?: 0,
            pid = state.pidLong ?: 0,
            startedAt = state.startedAt?.let { Instant.parse(it) },
            finishedAt = state.finishedAt?.let { Instant.parse(it) },
            error = state.error?.ifBlank { null },
            status = state.status!!,
            fsPath = inspect.config.volumes?.keys?.firstOrNull(),
            outOfMemory = state.oomKilled ?: false,
            mounts = inspect.mounts?.map { mount ->
                InstanceRuntimeMountImpl(
                    type = (mount.rawValues["Type"] as? String) ?: "volume",
                    target = mount.name.orEmpty(),
                    destination = mount.destination?.path.orEmpty(),
                    readonly = !(mount.rw ?: false)
                )
            }.orEmpty()
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
            connection = networkService.createConnection(host, port?.toInt()),
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

    private fun toInternalStats(statistics: Statistics): InstanceInternalStats? {
        val pid = statistics.pidsStats.current ?: return null
        val mem = statistics.memoryStats!!
        val cpu = statistics.cpuStats!!
        val last = statistics.preCpuStats

        return InstanceInternalStatsImpl(
            pid,
            mem.usage!!,
            mem.maxUsage!!,
            mem.limit!!,
            mem.stats!!.cache!!,
            cpu.cpuUsage!!.totalUsage!!,
            cpu.cpuUsage!!.percpuUsage!!.toLongArray(),
            cpu.systemCpuUsage!!,
            cpu.onlineCpus!!,
            last.cpuUsage?.totalUsage,
            last.cpuUsage?.percpuUsage?.toLongArray(),
            last.systemCpuUsage,
            last.onlineCpus
        )
    }
}
