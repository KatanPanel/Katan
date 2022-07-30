package org.katan.service.unit.instance.docker

import com.github.dockerjava.api.DockerClient
import kotlinx.coroutines.CoroutineName
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.coroutineScope
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

        private const val NETWORK_DRIVER_MACVLAN = "macvlan"
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
            client.removeContainerCmd(instance.runtimeId)
                .withRemoveVolumes(true)
                .withForce(true)
                .exec()
        }
    }

    override suspend fun stopInstance(instance: UnitInstance) {
        check(instance.status.isRunning()) {
            "Unit instance is not running, cannot be stopped (current status: %s)".format(
                instance.status.name
            )
        }

        withContext(Dispatchers.IO) {
            client.stopContainerCmd(instance.runtimeId).exec()
        }
    }

    override suspend fun startInstance(instance: UnitInstance) {
        check(!instance.status.isRunning()) {
            "Unit instance is already running, cannot be started again, stop it first (current status: %s)".format(
                instance.status.name
            )
        }

        withContext(Dispatchers.IO) {
            client.startContainerCmd(instance.runtimeId).exec()
        }
    }

    override suspend fun killInstance(instance: UnitInstance) {
        withContext(Dispatchers.IO) {
            client.killContainerCmd(instance.runtimeId).exec()
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

}