package org.katan.service.unit.instance.docker.model

import kotlinx.datetime.Instant
import org.katan.model.instance.InstanceRuntime
import org.katan.model.instance.InstanceRuntimeNetwork
import org.katan.model.instance.InstanceRuntimeSingleNetwork

internal data class InstanceRuntimeImpl(
    override val network: InstanceRuntimeNetwork,
    override val platform: String?,
    override val exitCode: Long,
    override val outOfMemory: Boolean,
    override val error: String?,
    override val status: String,
    override val pid: Long,
    override val startedAt: Instant?,
    override val finishedAt: Instant?
) : InstanceRuntime

internal data class InstanceRuntimeNetworkImpl(
    override val ipV4Address: String,
    override val hostname: String?,
    override val networks: List<InstanceRuntimeSingleNetwork>
) : InstanceRuntimeNetwork

internal data class InstanceRuntimeSingleNetworkImpl(
    override val id: String,
    override val name: String,
    override val ipv4Address: String?,
    override val ipv6Address: String?
) : InstanceRuntimeSingleNetwork