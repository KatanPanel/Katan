package me.devnatan.katan.backend.server

import kotlinx.coroutines.async
import me.devnatan.katan.backend.io.KProcess
import java.io.File

class KServer(
    val id: String,
    val path: File,
    val jar: File,
    val process: KProcess
) {

    fun startAsync() = process.coroutine.async {
        process.startAsync()
    }

}

