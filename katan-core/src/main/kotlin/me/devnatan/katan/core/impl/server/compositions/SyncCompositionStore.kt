package me.devnatan.katan.core.impl.server.compositions

import kotlinx.atomicfu.locks.SynchronizedObject
import me.devnatan.katan.api.composition.Composition
import me.devnatan.katan.api.composition.CompositionOptions
import me.devnatan.katan.api.composition.CompositionStore
import java.time.Instant

class SyncCompositionStore<T : CompositionOptions>(
    override val options: T,
    override val key: Composition.Key
) : CompositionStore<T>, SynchronizedObject() {

    override var lastModifiedAt: Instant? = null
        private set
        get() = field!!

    suspend fun update(block: suspend () -> Unit) {
        block()
        synchronized(this) {
            lastModifiedAt = Instant.now()
        }
    }

}