package org.katan.http.routes.unit_instance.dto

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import org.katan.model.unit.UnitInstance

@Serializable
internal data class UnitInstanceResponse(
    val id: Long,
    @SerialName("docker-image") val dockerImage: String,
    val status: String
) {
    constructor(instance: UnitInstance) : this(
        id = instance.id,
        dockerImage = instance.image,
        status = instance.status.name
    )

}