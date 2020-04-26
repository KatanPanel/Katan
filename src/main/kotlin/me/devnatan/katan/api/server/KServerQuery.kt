package me.devnatan.katan.api.server

import java.net.InetSocketAddress

open class KServerQuery(
    val address: InetSocketAddress,
    val version: String,
    val motd: String,
    val players: Int,
    val maxPlayers: Int,
    val latency: Long,
    val online: Boolean
) {

    object Empty : KServerQuery(InetSocketAddress.createUnresolved("0.0.0.0", 8080), "", "", 0, 0, 0, false)

}