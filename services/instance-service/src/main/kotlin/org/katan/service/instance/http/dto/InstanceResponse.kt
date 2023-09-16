package org.katan.service.instance.http.dto

import kotlinx.datetime.Instant
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import org.katan.model.instance.InstanceRuntime
import org.katan.model.instance.InstanceRuntimeMount
import org.katan.model.instance.InstanceRuntimeSingleNetwork
import org.katan.model.instance.UnitInstance
import org.katan.model.net.HostPort

@Serializable
data class InstanceResponse internal constructor(
    val id: String,
    @SerialName("update-policy") val updatePolicy: String,
    val status: String,
    @SerialName("container-id") val containerId: String?,
    @SerialName("blueprint-id") val blueprintId: String?,
    val connection: InstanceConnectionResponse?,
    val runtime: InstanceRuntimeResponse?,
) {

    internal constructor(instance: UnitInstance) : this(
        id = instance.id.value.toString(),
        updatePolicy = instance.updatePolicy.id,
        containerId = instance.containerId,
        status = instance.status.value,
        blueprintId = instance.blueprintId.value.toString(),
        connection = instance.connection?.let(::InstanceConnectionResponse),
        runtime = instance.runtime?.let(::InstanceRuntimeResponse),
    )
}

@Serializable
data class InstanceRuntimeResponse internal constructor(
    val status: String,
    @SerialName("oom") val outOfMemory: Boolean,
    val error: String?,
    val platform: String?,
    @SerialName("exit-code") val exitCode: Int,
    val pid: Int,
    @SerialName("started-at") val startedAt: Instant? = null,
    @SerialName("finished-at") val finishedAt: Instant? = null,
    val network: InstanceRuntimeNetworkResponse,
    val mounts: List<InstanceRuntimeMountResponse>,
) {

    internal constructor(runtime: InstanceRuntime) : this(
        status = runtime.status,
        outOfMemory = runtime.outOfMemory,
        error = runtime.error,
        platform = runtime.platform,
        exitCode = runtime.exitCode,
        pid = runtime.pid,
        startedAt = runtime.startedAt,
        finishedAt = runtime.finishedAt,
        network = InstanceRuntimeNetworkResponse(
            ipAddress = runtime.network.ipV4Address.ifBlank { null },
            hostname = runtime.network.hostname,
            networks = runtime.network.networks.map(::InstanceRuntimeSingleNetworkResponse),
        ),
        mounts = runtime.mounts.map(::InstanceRuntimeMountResponse),
    )
}

@Serializable
data class InstanceRuntimeNetworkResponse internal constructor(
    @SerialName("ipv4-address") val ipAddress: String?,
    val hostname: String?,
    val networks: List<InstanceRuntimeSingleNetworkResponse>,
)

@Serializable
data class InstanceConnectionResponse internal constructor(
    val host: String,
    val port: Short,
) {

    internal constructor(connection: HostPort) : this(
        host = connection.host,
        port = connection.port,
    )
}

@Serializable
data class InstanceRuntimeSingleNetworkResponse internal constructor(
    val id: String,
    val name: String,
    @SerialName("ipv4-address") val ipv4Address: String?,
    @SerialName("ipv6-address") val ipv6Address: String?,
) {

    internal constructor(network: InstanceRuntimeSingleNetwork) : this(
        id = network.id,
        name = network.name,
        ipv4Address = network.ipv4Address,
        ipv6Address = network.ipv6Address,
    )
}

@Serializable
data class InstanceRuntimeMountResponse internal constructor(
    val type: String,
    val dest: String,
    val target: String,
    val readonly: Boolean,
) {

    internal constructor(mount: InstanceRuntimeMount) : this(
        type = mount.type,
        dest = mount.destination,
        target = mount.target,
        readonly = mount.readonly,
    )
}
