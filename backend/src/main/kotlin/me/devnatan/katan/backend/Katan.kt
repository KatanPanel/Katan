package me.devnatan.katan.backend

import kotlinx.coroutines.CoroutineName
import kotlinx.coroutines.CoroutineScope
import me.devnatan.katan.backend.server.KServerManager
import org.slf4j.Logger

object Katan {

    lateinit var logger: Logger
    lateinit var serverManager: KServerManager
    private val coroutine: CoroutineScope = CoroutineScope(CoroutineName("Katan"))

    internal fun init(logger: Logger) {
        this.logger = logger
        serverManager = KServerManager(logger)
        serverManager.load(coroutine)
    }

}