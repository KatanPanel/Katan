package me.devnatan.katan.api.process

import me.devnatan.katan.api.EmptySuspendBlock
import me.devnatan.katan.api.SuspendBlock

class ProcessHandler {

    var onStart: EmptySuspendBlock? = null

    var onStop: EmptySuspendBlock? = null

    var onMessage: SuspendBlock<String>? = null

}