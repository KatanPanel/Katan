package org.katan.model.instance

import kotlinx.datetime.Instant
import org.katan.model.Snowflake
import org.katan.model.io.HostPort
import org.katan.model.unit.ImageUpdatePolicy

public interface UnitInstance {
    public val id: Snowflake

    public val status: InstanceStatus

    public val containerId: String?

    public val updatePolicy: ImageUpdatePolicy

    public val connection: HostPort?

    public val runtime: InstanceRuntime?

    public val blueprintId: Snowflake

    public val createdAt: Instant
}
