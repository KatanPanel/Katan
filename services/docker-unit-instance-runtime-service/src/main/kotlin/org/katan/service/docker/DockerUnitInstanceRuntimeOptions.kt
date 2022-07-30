package org.katan.service.docker

import org.katan.service.unit.instance.runtime.UnitInstanceRuntimeOptions

internal data class DockerUnitInstanceRuntimeOptions(
    val name: String
) : UnitInstanceRuntimeOptions