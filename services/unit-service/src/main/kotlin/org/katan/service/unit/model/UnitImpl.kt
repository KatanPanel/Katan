package org.katan.service.unit.model

import kotlinx.datetime.Instant
import org.katan.model.Snowflake
import org.katan.model.unit.KUnit
import org.katan.model.unit.UnitStatus

internal data class UnitImpl(
    override val id: Snowflake,
    override val externalId: String?,
    override val instanceId: Snowflake?,
    override val nodeId: Int,
    override val name: String,
    override val createdAt: Instant,
    override val updatedAt: Instant,
    override val deletedAt: Instant?,
    override val status: UnitStatus
) : KUnit
