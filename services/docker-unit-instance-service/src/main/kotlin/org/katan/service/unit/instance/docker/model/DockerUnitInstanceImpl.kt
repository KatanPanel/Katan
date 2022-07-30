package org.katan.service.unit.instance.docker.model

import org.katan.model.unit.UnitInstance
import org.katan.model.unit.UnitInstanceStatus

internal data class DockerUnitInstanceImpl(
    override val id: Long,
    override val status: UnitInstanceStatus,
    val imageId: String,
    val containerId: String
) : UnitInstance