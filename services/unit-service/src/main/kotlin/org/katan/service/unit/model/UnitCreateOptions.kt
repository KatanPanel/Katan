package org.katan.service.unit.model

public data class UnitCreateOptions(
    val name: String,
    val network: Network,
    val externalId: String?,
    val dockerImage: String,
    val actorId: Long?
) {

    public data class Network(
        val host: String?,
        val port: Int?
    )
}
