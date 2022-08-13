package org.katan.service.unit.instance.http.dto

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import org.katan.model.instance.UnitInstance

@Serializable
public data class InstanceResponse(
    val id: String,
    @SerialName("image_update_policy") val imageUpdatePolicy: String,
    val status: String
) {

    public constructor(instance: UnitInstance) : this(
        id = instance.id.toString(),
        imageUpdatePolicy = instance.imageUpdatePolicy.id,
        status = instance.status.name
    )
}
