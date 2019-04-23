package me.devnatan.katan.backend.server

import kotlinx.coroutines.async
import me.devnatan.katan.backend.io.KProcess

class KServer(
    val id: String,
    val path: KServerPath,
    val process: KProcess,
    var state: EnumKServerState
) {

    fun startAsync() = process.coroutine.async {
        process.startAsync()
    }

}

