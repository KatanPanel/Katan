package org.katan.service.instance.model

import kotlinx.datetime.Instant
import org.katan.model.instance.InstanceRuntime
import org.katan.model.instance.InstanceRuntimeMount
import org.katan.model.instance.InstanceRuntimeNetwork
import org.katan.model.instance.InstanceRuntimeSingleNetwork

internal data class InstanceRuntimeImpl(
    override val id: String,
    override val network: InstanceRuntimeNetwork,
    override val platform: String?,
    override val exitCode: Int,
    override val outOfMemory: Boolean,
    override val error: String?,
    override val status: String,
    override val pid: Int,
    override val fsPath: String?,
    override val startedAt: Instant?,
    override val finishedAt: Instant?,
    override val mounts: List<InstanceRuntimeMount>,
) : InstanceRuntime

internal data class InstanceRuntimeNetworkImpl(
    override val ipV4Address: String,
    override val hostname: String?,
    override val networks: List<InstanceRuntimeSingleNetwork>,
) : InstanceRuntimeNetwork

internal data class InstanceRuntimeSingleNetworkImpl(
    override val id: String,
    override val name: String,
    override val ipv4Address: String?,
    override val ipv6Address: String?,
) : InstanceRuntimeSingleNetwork

internal data class InstanceRuntimeMountImpl(
    override val type: String,
    override val target: String,
    override val destination: String,
    override val readonly: Boolean,
) : InstanceRuntimeMount
