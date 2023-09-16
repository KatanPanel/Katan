package org.katan.model.instance

import kotlinx.datetime.Instant
import kotlinx.serialization.Serializable
import org.katan.model.Snowflake
import org.katan.model.net.HostPort
import org.katan.model.unit.ImageUpdatePolicy

@Serializable
class UnitInstance(
    val id: Snowflake,
    val status: InstanceStatus,
    val containerId: String?,
    val updatePolicy: ImageUpdatePolicy,
    val connection: HostPort?,
    val runtime: InstanceRuntime?,
    val blueprintId: Snowflake,
    val createdAt: Instant,
)

val UnitInstance.containerIdOrThrow: String
    get() = containerId ?: throw InstanceUnreachableRuntimeException()

val UnitInstance.runtimeOrThrow: InstanceRuntime
    get() = runtime ?: throw InstanceUnreachableRuntimeException()
