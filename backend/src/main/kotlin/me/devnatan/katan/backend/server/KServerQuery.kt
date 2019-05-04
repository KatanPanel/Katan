package me.devnatan.katan.backend.server

data class KServerQuery(
    val address: String,
    val port: Int,
    val version: String,
    val motd: String,
    val players: Int,
    val maxPlayers: Int,
    val latency: Long,
    val online: Boolean = true
) {

    companion object {

        @JvmStatic
        fun offline() = KServerQuery("", 0, "", "", 0, 0, 0, false)

    }

}