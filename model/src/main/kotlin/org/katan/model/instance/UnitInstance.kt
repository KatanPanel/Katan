package org.katan.model.instance

import org.katan.model.io.HostPort
import org.katan.model.unit.ImageUpdatePolicy

interface UnitInstance {
    val id: Long

    val status: InstanceStatus

    val containerId: String?

    val updatePolicy: ImageUpdatePolicy

    val connection: HostPort?

    val runtime: InstanceRuntime?

    val blueprintId: Long
}
