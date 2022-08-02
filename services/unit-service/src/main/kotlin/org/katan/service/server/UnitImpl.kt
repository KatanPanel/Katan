package org.katan.service.server

import kotlinx.datetime.Instant
import org.katan.model.unit.KUnit
import org.katan.model.unit.UnitInstance

public data class UnitImpl(
    override val id: Long,
    override val externalId: String?,
    override val nodeId: Int,
    override val name: String,
    override val displayName: String?,
    override val description: String?,
    override val createdAt: Instant,
    override val updatedAt: Instant,
    override val instance: UnitInstance,
//    override val status: UnitStatus = UnitStatus.Unknown
    override val deletedAt: Instant? = null
) : KUnit
