package org.katan.service.unit.model

data class UnitCreateOptions(
    val name: String,
    val blueprint: Long,
    val network: Network,
    val externalId: String?,
    val actorId: Long?
) {

    data class Network(
        val host: String?,
        val port: Int?
    )
}
