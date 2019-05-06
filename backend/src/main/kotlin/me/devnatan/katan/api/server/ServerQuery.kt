package me.devnatan.katan.api.server

import java.net.InetSocketAddress

class ServerQuery(
    val address: InetSocketAddress,
    val version: String,
    val motd: String,
    val players: Int,
    val maxPlayers: Int,
    val latency: Long,
    val online: Boolean = true
)