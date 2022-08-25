package org.katan.http.server.dto

import kotlinx.datetime.Instant
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
internal data class ServerInfoResponse(
    val version: String,
    val build: ServerInfoBuild,
    @SerialName("node-id") val nodeId: Int,
    @SerialName("cluster-mode") val clusterMode: Boolean,
    @SerialName("default-network") val defaultNetwork: ServerInfoNetworkResponse
)

@Serializable
internal data class ServerInfoNetworkResponse(
    val name: String,
    val driver: String
)

@Serializable
internal data class ServerInfoBuild(
    val commit: String,
    val message: String,
    val time: Instant,
    val branch: String,
    val remote: String
)
