package org.katan.http.routes.unit.dto

import kotlinx.datetime.Instant
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import org.katan.http.routes.unit_instance.dto.UnitInstanceResponse
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
    val instance: UnitInstanceResponse?,
    val status: String
) {

    constructor(value: KUnit): this(
        id = value.id,
        externalId = value.externalId,
        nodeId = value.nodeId,
        name = value.name,
        displayName = value.displayName,
        description = value.description,
        createdAt = value.createdAt,
        updatedAt = value.updatedAt,
        deletedAt = value.deletedAt,
        instance = value.instance?.let { UnitInstanceResponse(it) },
        status = value.status.value
    )

}