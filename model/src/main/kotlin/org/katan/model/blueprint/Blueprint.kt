package org.katan.model.blueprint

import kotlinx.datetime.Instant
import org.katan.model.Snowflake

interface Blueprint {

    val id: Snowflake

    val createdAt: Instant

    val updatedAt: Instant

    val spec: BlueprintSpec
}
