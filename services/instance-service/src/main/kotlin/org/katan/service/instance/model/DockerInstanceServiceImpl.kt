package org.katan.service.instance.model

import kotlinx.coroutines.CoroutineName
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.onCompletion
import kotlinx.coroutines.launch
import kotlinx.datetime.Clock
import me.devnatan.yoki.Yoki
import me.devnatan.yoki.models.exec.ExecCreateOptions
import me.devnatan.yoki.models.exec.ExecStartOptions
import me.devnatan.yoki.resource.container.create
import me.devnatan.yoki.resource.container.remove
import me.devnatan.yoki.resource.image.ImageNotFoundException
import org.apache.logging.log4j.LogManager
import org.katan.EventsDispatcher
import org.katan.KatanConfig
import org.katan.model.Snowflake
import org.katan.model.instance.InstanceInternalStats
import org.katan.model.instance.InstanceNotFoundException
import org.katan.model.instance.InstanceRuntime
import org.katan.model.instance.InstanceRuntimeNetwork
import org.katan.model.instance.InstanceStatus
import org.katan.model.instance.InstanceUpdateCode
import org.katan.model.instance.UnitInstance
import org.katan.model.instance.containerIdOrThrow
import org.katan.model.instance.runtimeOrThrow
import org.katan.model.net.HostPort
import org.katan.model.toSnowflake
import org.katan.model.unit.ImageUpdatePolicy
import org.katan.service.blueprint.BlueprintService
import org.katan.service.id.IdService
import org.katan.service.instance.InstanceCreatedEvent
import org.katan.service.instance.InstanceService
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
    private val eventsDispatcher: EventsDispatcher,
    private val idService: IdService,
    private val networkService: NetworkService,
    private val blueprintService: BlueprintService,
    private val dockerClient: Yoki,
    private val instanceRepository: InstanceRepository,
    private val config: KatanConfig,
) : InstanceService,
    CoroutineScope by CoroutineScope(
        SupervisorJob() +
            CoroutineName(DockerInstanceServiceImpl::class.jvmName),
    ) {

    private companion object {
        private val logger = LogManager.getLogger(DockerInstanceServiceImpl::class.java)

        private const val BASE_LABEL = "org.katan.instance."
    }

    init {
        DockerEventScope(dockerClient, eventsDispatcher, coroutineContext)
    }

    override suspend fun getInstance(id: Snowflake): UnitInstance {
        // TODO cache service
        return instanceRepository.findById(id)?.toDomain()
            ?: throw InstanceNotFoundException()
    }

    override suspend fun getInstanceLogs(id: Snowflake): Flow<String> {
        val instance = getInstance(id)
        val runtime = instance.runtimeOrThrow

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

    override suspend fun runInstanceCommand(id: Snowflake, command: String) {
        val instance = getInstance(id)
        val execId = dockerClient.containers.exec(
            container = instance.containerIdOrThrow,
            options = ExecCreateOptions().apply {
                this.command = command.split(" ")
                tty = true
                attachStdin = false
                attachStdout = false
                attachStderr = false
            },
        )

        dockerClient.exec.start(
            id = execId,
            options = ExecStartOptions().apply {
                detach = true
            },
        )
    }

    override suspend fun streamInternalStats(id: Snowflake): Flow<InstanceInternalStats> {
        val instance = getInstance(id)
        val runtime = instance.runtimeOrThrow

        // TODO implement /containers/:id/stats on Yoki
        // TODO handle docker client calls properly
        return flow { }
    }

    override suspend fun deleteInstance(instance: UnitInstance) {
        instanceRepository.delete(instance.id)

        instance.containerId?.also { containerId ->
            dockerClient.containers.remove(containerId) {
                removeAnonymousVolumes = true
                force = true
            }
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
        val containerId = instance.containerIdOrThrow

        // container will be deleted so restart command will fail
        if (tryUpdateImage(containerId, instance.updatePolicy)) {
            return
        }

        dockerClient.containers.restart(containerId)
    }

    private suspend fun tryUpdateImage(containerId: String, updatePolicy: ImageUpdatePolicy): Boolean {
        // fast path -- ignore image update if policy is set to Never
        if (updatePolicy == ImageUpdatePolicy.Never) {
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

        dockerClient.images.pull(currImage).collect {
            logger.debug("Pulling image... {}", it)
        }
        return true
    }

    private suspend fun updateInstance(id: Snowflake, status: InstanceStatus) {
        instanceRepository.update(id) {
            this.status = status.value
        }
    }

    // TODO check for parameters invalid property types
    override suspend fun updateInstanceStatus(instance: UnitInstance, code: InstanceUpdateCode) {
        val containerId = requireNotNull(instance.containerId) {
            "Cannot update non-initialized instance container"
        }

        when (code) {
            InstanceUpdateCode.Start -> startInstance(containerId, instance.status)
            InstanceUpdateCode.Stop -> stopInstance(containerId, instance.status)
            InstanceUpdateCode.Restart -> restartInstance(instance)
            InstanceUpdateCode.Kill -> killInstance(containerId)
        }
    }

    override suspend fun createInstance(blueprintId: Snowflake, options: CreateInstanceOptions): UnitInstance {
        val blueprint = blueprintService.getBlueprint(blueprintId)
        val instanceId = idService.generate()
        val generatedName = generateContainerName(instanceId, blueprint.spec.build?.instance?.name)

        return try {
            val containerId = createContainer(instanceId, options.image, generatedName)
            val connection = connectInstance(containerId, options.host, options.port)
            registerInstance(
                instanceId = instanceId,
                blueprintId = blueprint.id,
                status = statusFromConnection(connection),
                containerId = containerId,
                connection = connection,
            )
        } catch (e: ImageNotFoundException) {
            val instance = registerInstance(
                instanceId = instanceId,
                blueprintId = blueprint.id,
                status = InstanceStatus.ImagePullNeeded,
                containerId = null,
                connection = null,
            )

            setupInstanceAsync(
                instanceId = instance.id,
                instanceName = generatedName,
                options = options,
            )

            instance
        }
    }

    private fun setupInstanceAsync(instanceId: Snowflake, instanceName: String, options: CreateInstanceOptions) = launch(Dispatchers.Default) {
        dockerClient.images.pull(options.image)
            .onCompletion { error ->
                val status = if (error != null) {
                    InstanceStatus.ImagePullFailed
                } else {
                    InstanceStatus.ImagePullCompleted
                }

                logger.debug("Image {} pull completed.", options.image)
                updateInstance(instanceId, status)
            }
            .catch { error -> logger.error("Failed to pull image: ${options.image}", error) }
            .collect { pull -> logger.debug("Pulling image {}: {}", options.image, pull) }

        val container = createContainer(instanceId, options.image, instanceName)
        val connection = connectInstance(container, options.host, options.port)

        instanceRepository.update(instanceId) {
            this.containerId = containerId
            this.status = statusFromConnection(connection).value
        }
    }

    private fun statusFromConnection(connection: HostPort?): InstanceStatus =
        if (connection == null) InstanceStatus.NetworkAssignmentFailed else InstanceStatus.Created

    private suspend fun connectInstance(
        containerId: String,
        host: String?,
        port: Int?,
    ): HostPort? {
        logger.debug("Connecting $containerId to ${config.dockerNetwork}...")
        return runCatching {
            val connection = networkService.connect(
                network = config.dockerNetwork,
                instance = containerId,
                host = host,
                port = port?.toShort(),
            )
            logger.debug("Connected {} to {} @ {}", containerId, config.dockerNetwork, connection)
            connection
        }.onFailure { error ->
            logger.error("Unable to connect $containerId to the network ${config.dockerNetwork}", error)
        }.getOrNull()
    }

    private suspend fun registerInstance(
        instanceId: Snowflake,
        blueprintId: Snowflake,
        status: InstanceStatus,
        containerId: String?,
        connection: HostPort?,
    ): UnitInstance {
        val instance = UnitInstance(
            id = instanceId,
            status = status,
            updatePolicy = ImageUpdatePolicy.Always,
            containerId = containerId,
            connection = connection,
            runtime = containerId?.let { buildRuntime(it) },
            blueprintId = blueprintId,
            createdAt = Clock.System.now(),
        )

        instanceRepository.create(instance)
        eventsDispatcher.dispatch(
            InstanceCreatedEvent(
                instanceId = instance.id,
                blueprintId = instance.blueprintId,
                createdAt = instance.createdAt,
            ),
        )
        return instance
    }

    private fun generateContainerName(instanceId: Snowflake, nameFormat: String?): String =
        (nameFormat ?: "katan-{node}-{id}")
            .replace("{id}", instanceId.value.toString())
            .replace("{node}", config.nodeId.toString())

    private suspend fun createContainer(instanceId: Snowflake, image: String, name: String): String {
        logger.debug("Creating container with $image to $instanceId...")
        return dockerClient.containers.create {
            this.image = image
            this.name = name
            labels = mapOf("id" to BASE_LABEL + instanceId.value)
        }
    }

    private suspend fun buildRuntime(containerId: String): InstanceRuntime {
        val inspection = dockerClient.containers.inspect(containerId)
//        val networkSettings = inspection.networkSettings
        val state = inspection.state

        return InstanceRuntime(
            id = inspection.id,
            network = InstanceRuntimeNetwork(
                ipV4Address = "",
                hostname = null,
                networks = emptyList(),
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
            mounts = emptyList(),
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

    private suspend fun InstanceEntity.toDomain(): UnitInstance = UnitInstance(
        id = getId().toSnowflake(),
        updatePolicy = ImageUpdatePolicy.getById(updatePolicy),
        containerId = containerId,
        status = toStatus(status),
        connection = networkService.createConnection(host, port),
        runtime = containerId?.let { buildRuntime(it) },
        blueprintId = blueprintId.toSnowflake(),
        createdAt = createdAt,
    )

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
