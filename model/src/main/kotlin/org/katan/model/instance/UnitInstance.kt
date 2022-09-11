package org.katan.model.instance

import org.katan.model.net.Connection
import org.katan.model.unit.ImageUpdatePolicy

interface UnitInstance {
    val id: Long

    val status: InstanceStatus

    val containerId: String?

    val updatePolicy: ImageUpdatePolicy

    val connection: Connection?

    val runtime: InstanceRuntime?

    val blueprintId: Long
}
