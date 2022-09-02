package org.katan.model.instance

import kotlinx.datetime.Instant

interface InstanceRuntime {

    val status: String

    val exitCode: Long

    val pid: Long

    val outOfMemory: Boolean

    val error: String?

    val startedAt: Instant?

    val finishedAt: Instant?

    val platform: String?

    val fsPath: String?

    val network: InstanceRuntimeNetwork

    val mounts: List<InstanceRuntimeMount>
}

interface InstanceRuntimeNetwork {

    val ipV4Address: String

    val hostname: String?

    val networks: List<InstanceRuntimeSingleNetwork>
}

interface InstanceRuntimeSingleNetwork {

    val id: String

    val name: String

    val ipv4Address: String?

    val ipv6Address: String?
}

interface InstanceRuntimeMount {

    val type: String

    val target: String

    val destination: String

    val readonly: Boolean
}
