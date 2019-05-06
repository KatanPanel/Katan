package me.devnatan.katan.backend.server.query;

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.newSingleThreadContext
import me.devnatan.katan.backend.katan
import me.devnatan.katan.backend.util.timer

class ServerQueryTask : Runnable {

    private val executor = newSingleThreadContext("Katan:Query")
    private val context = CoroutineScope(executor)

    override fun run() {
        context.timer(5000, false) {
            katan.serverController.servers.filter {
                it.state.isRunning
            }.forEach {
                it.query = ServerQueryHelper.query(it.query!!.address)
            }
        }
    }

}
