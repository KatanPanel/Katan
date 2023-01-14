package org.katan.http.server.dto

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
internal data class ServerInfoResponse(
    val version: String,
    val build: ServerInfoBuild,
    @SerialName("node-id") val nodeId: Int
)

@Serializable
internal data class ServerInfoBuild(
    val commit: String,
    val branch: String
)
