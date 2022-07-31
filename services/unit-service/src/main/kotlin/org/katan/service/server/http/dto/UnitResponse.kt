package org.katan.service.server.http.dto

import kotlinx.datetime.Instant
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import org.katan.model.unit.KUnit
import org.katan.model.unit.UnitInstance

@Serializable
internal data class UnitResponse(
    val id: Long,
    @SerialName("external-id") val externalId: String?,
    @SerialName("node-id") val nodeId: Int,
    val name: String,
    @SerialName("display-name") val displayName: String?,
    val description: String?,
    @SerialName("created-at") val createdAt: Instant,
    @SerialName("updated-at") val updatedAt: Instant,
    @SerialName("deleted-at") val deletedAt: Instant?,
    val instance: UnitInstance?,
    val status: String
) {

    constructor(value: KUnit) : this(
        id = value.id,
        externalId = value.externalId,
        nodeId = value.nodeId,
        name = value.name,
        displayName = value.displayName,
        description = value.description,
        createdAt = value.createdAt,
        updatedAt = value.updatedAt,
        deletedAt = value.deletedAt,
        instance = value.instance,
        status = value.status.value
    )
}
