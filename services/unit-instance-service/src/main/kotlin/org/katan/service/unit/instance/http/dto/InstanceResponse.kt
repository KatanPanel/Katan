package org.katan.service.unit.instance.http.dto

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import org.katan.model.instance.UnitInstance
import org.katan.model.net.Connection

@Serializable
public data class InstanceResponse(
    val id: String,
    @SerialName("update_policy") val updatePolicy: String,
    val status: String,
    @SerialName("container-id") val containerId: String?,
    val connection: InstanceConnectionResponse?
) {

    public constructor(instance: UnitInstance) : this(
        id = instance.id.toString(),
        updatePolicy = instance.updatePolicy.id,
        containerId = instance.containerId,
        status = instance.status.value,
        connection = instance.connection?.let(::InstanceConnectionResponse)
    )
}

@Serializable
public data class InstanceConnectionResponse(
    val host: String,
    val port: Int
) {

    public constructor(connection: Connection) : this(
        host = connection.host,
        port = connection.port
    )
}
