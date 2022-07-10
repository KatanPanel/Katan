package org.katan.service.unit.instance.docker

import org.katan.service.unit.instance.UnitInstanceCreationResult
import org.katan.service.unit.instance.UnitInstanceService
import org.katan.service.unit.instance.UnitInstanceSpec

internal class DockerUnitInstanceServerImpl : UnitInstanceService {

    override suspend fun createInstanceFor(spec: UnitInstanceSpec): UnitInstanceCreationResult {
        TODO("Not yet implemented")
    }

}