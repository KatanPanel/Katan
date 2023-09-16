package org.katan.model.instance

import kotlinx.datetime.Instant

public interface InstanceRuntime {

    public val id: String

    public val status: String

    public val exitCode: Int

    public val pid: Int

    public val outOfMemory: Boolean

    public val error: String?

    public val startedAt: Instant?

    public val finishedAt: Instant?

    public val platform: String?

    public val fsPath: String?

    public val network: InstanceRuntimeNetwork

    public val mounts: List<InstanceRuntimeMount>
}

public interface InstanceRuntimeNetwork {

    public val ipV4Address: String

    public val hostname: String?

    public val networks: List<InstanceRuntimeSingleNetwork>
}

public interface InstanceRuntimeSingleNetwork {

    public val id: String

    public val name: String

    public val ipv4Address: String?

    public val ipv6Address: String?
}

public interface InstanceRuntimeMount {

    public val type: String

    public val target: String

    public val destination: String

    public val readonly: Boolean
}
