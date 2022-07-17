package org.katan.service.unit.instance.docker

import com.github.dockerjava.api.DockerClient
import kotlinx.coroutines.CoroutineName
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.withContext
import org.apache.logging.log4j.LogManager
import org.katan.model.Connection
import org.katan.model.unit.UnitInstance
import org.katan.service.unit.instance.UnitInstanceCreationResult
import org.katan.service.unit.instance.UnitInstanceService
import org.katan.service.unit.instance.UnitInstanceSpec
import kotlin.reflect.jvm.jvmName

internal class DockerUnitInstanceServiceImpl : UnitInstanceService, CoroutineScope by CoroutineScope(
    CoroutineName(DockerUnitInstanceServiceImpl::class.jvmName)
) {

    private companion object {
        private val logger = LogManager.getLogger(DockerUnitInstanceServiceImpl::class.java)
    }

    private lateinit var client: DockerClient

    override fun fromSpec(data: Map<String, Any>): UnitInstanceSpec {
        println("before data check: $data")
        check(data.containsKey(IMAGE_PROPERTY)) { "Missing required property \"$IMAGE_PROPERTY\"." }

        println("from spec")
        return DockerUnitInstanceSpec(data.getValue(IMAGE_PROPERTY) as String)
    }

    override suspend fun createInstanceFor(spec: UnitInstanceSpec): UnitInstance {
        require(spec is DockerUnitInstanceSpec) { "Instance spec must be a DockerUnitInstanceSpec" }

        logger.info("Generating a unit instance: $spec")
        return coroutineScope {
            // TODO better context switch
            val id = withContext(Dispatchers.IO) {
                client.createContainerCmd(
                    spec.image
                ).exec()
            }.id

            logger.info("Unit instance generated successfully: $id")
            val container = withContext(Dispatchers.IO) { client.inspectContainerCmd(id).exec() }
            container.networkSettings.ports.bindings.entries.first().key.

            logger.info("Generated instance name: ${container.name}")
            object: UnitInstance {
                override val remoteAddress: Connection
                    get() = object : Connection {
                        override val host: String
                            get() = container.networkSettings.ipAddress

                        override val port: Short
                            get() = container
                    }
            }
        }
    }

}