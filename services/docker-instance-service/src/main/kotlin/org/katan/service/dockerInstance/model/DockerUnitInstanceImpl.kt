package org.katan.service.dockerInstance.model

import org.katan.model.instance.InstanceRuntime
import org.katan.model.instance.InstanceStatus
import org.katan.model.instance.UnitInstance
import org.katan.model.net.Connection
import org.katan.model.unit.ImageUpdatePolicy

internal data class DockerUnitInstanceImpl(
    override val id: Long,
    override val status: InstanceStatus,
    override val updatePolicy: ImageUpdatePolicy,
    override val containerId: String?,
    override val connection: Connection?,
    override val runtime: InstanceRuntime?
) : UnitInstance
