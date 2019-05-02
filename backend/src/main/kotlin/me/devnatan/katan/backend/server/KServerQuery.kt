package me.devnatan.katan.backend.server

data class KServerQuery(
    val address: Pair<String, Int>,
    val version: String,
    val motd: String,
    val players: Int,
    val maxPlayers: Int,
    val latency: Long,
    val online: Boolean = true
) {

    companion object {

        @JvmStatic
        fun offline() = KServerQuery("" to 0, "", "", 0, 0, 0, false)

    }

}