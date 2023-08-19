package org.katan.service.unit.model

import org.katan.model.Snowflake

public data class UnitCreateOptions(
    val name: String,
    val blueprintId: Snowflake,
    val image: String,
    val options: Map<String, String>,
    val network: Network?,
    val externalId: String?,
    val actorId: Snowflake?
) {

    public data class Network(val host: String?, val port: Int?)
}
