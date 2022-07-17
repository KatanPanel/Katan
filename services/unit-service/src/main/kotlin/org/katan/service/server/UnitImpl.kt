package org.katan.service.server

import kotlinx.datetime.Instant
import kotlinx.serialization.Serializable
import org.katan.model.unit.KUnit
import org.katan.model.unit.UnitInstance
import org.katan.model.unit.UnitStatus

@Serializable
public data class UnitImpl(
    override val id: Long,
    override val externalId: String?,
    override val nodeId: Int,
    override val name: String,
    override val displayName: String?,
    override val description: String?,
    override val createdAt: Instant,
    override val updatedAt: Instant,
    override val instance: UnitInstance
) : KUnit {

    override val deletedAt: Instant? = null
    override val status: UnitStatus = UnitStatus.Unknown

}