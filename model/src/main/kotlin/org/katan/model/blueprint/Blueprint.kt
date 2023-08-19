package org.katan.model.blueprint

import kotlinx.datetime.Instant
import org.katan.model.Snowflake

public interface Blueprint {

    public val id: Snowflake

    public val createdAt: Instant

    public val updatedAt: Instant

    public val spec: BlueprintSpec
}
