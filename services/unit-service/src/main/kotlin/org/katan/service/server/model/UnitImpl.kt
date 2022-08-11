package org.katan.service.server.model

import kotlinx.datetime.Instant
import org.katan.model.unit.KUnit

internal data class UnitImpl(
    override val id: Long,
    override val externalId: String?,
    override val instanceId: Long,
    override val nodeId: Int,
    override val name: String,
    override val createdAt: Instant,
    override val updatedAt: Instant,
    override val deletedAt: Instant? = null
) : KUnit
