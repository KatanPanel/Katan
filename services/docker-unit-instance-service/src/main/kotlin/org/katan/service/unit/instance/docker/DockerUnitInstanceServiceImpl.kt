package org.katan.service.unit.instance.docker

import com.github.dockerjava.api.DockerClient
import org.apache.logging.log4j.LogManager
import org.katan.model.Connection
import org.katan.service.unit.instance.UnitInstanceCreationResult
import org.katan.service.unit.instance.UnitInstanceService
import org.katan.service.unit.instance.UnitInstanceSpec

public class DockerUnitInstanceSpec(
    public val image: String
) : UnitInstanceSpec

public class DockerUnitInstanceServiceImpl : UnitInstanceService {

    private companion object {
        private val logger = LogManager.getLogger(DockerUnitInstanceServiceImpl::class.java)
    }

    private lateinit var client: DockerClient

    override suspend fun createInstanceFor(spec: UnitInstanceSpec): UnitInstanceCreationResult {
        require(spec is DockerUnitInstanceSpec) { "Spec must be a DockerUnitInstanceSpec" }

        val id = client.createContainerCmd(spec.image).exec().id
        val container = client.inspectContainerCmd(id).exec()

        return object : UnitInstanceCreationResult {
            override val address: Connection
                get() = object : Connection {
                    override val host: String
                        get() = container.networkSettings.ipAddress

                    override val port: Short
                        get() = 25565
                }
        }
    }

}