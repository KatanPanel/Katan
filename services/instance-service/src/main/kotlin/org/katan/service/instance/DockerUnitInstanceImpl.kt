package org.katan.service.instance

import org.katan.model.Snowflake
import org.katan.model.instance.InstanceRuntime
import org.katan.model.instance.InstanceStatus
import org.katan.model.instance.UnitInstance
import org.katan.model.io.HostPort
import org.katan.model.unit.ImageUpdatePolicy

internal data class DockerUnitInstanceImpl(
    override val id: Long,
    override val status: InstanceStatus,
    override val updatePolicy: ImageUpdatePolicy,
    override val containerId: String?,
    override val connection: HostPort?,
    override val runtime: InstanceRuntime?,
    override val blueprintId: Snowflake
) : UnitInstance
