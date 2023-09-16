package org.katan.model.instance

import kotlinx.datetime.Instant
import kotlinx.serialization.Serializable

@Serializable
internal data class InstanceRuntime(
    val id: String,
    val network: InstanceRuntimeNetwork,
    val platform: String?,
    val exitCode: Int,
    val outOfMemory: Boolean,
    val error: String?,
    val status: String,
    val pid: Int,
    val fsPath: String?,
    val startedAt: Instant?,
    val finishedAt: Instant?,
    val mounts: List<InstanceRuntimeMount>,
)

@Serializable
internal data class InstanceRuntimeNetwork(
    val ipV4Address: String,
    val hostname: String?,
    val networks: List<InstanceRuntimeSingleNetwork>,
)

@Serializable
internal data class InstanceRuntimeSingleNetwork(
    val id: String,
    val name: String,
    val ipv4Address: String?,
    val ipv6Address: String?,
)

@Serializable
internal data class InstanceRuntimeMount(
    val type: String,
    val target: String,
    val destination: String,
    val readonly: Boolean,
)