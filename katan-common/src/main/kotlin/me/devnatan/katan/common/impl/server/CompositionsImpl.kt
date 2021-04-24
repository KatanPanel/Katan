package me.devnatan.katan.common.impl.server

import me.devnatan.katan.api.composition.Composition
import me.devnatan.katan.api.composition.CompositionOptions
import me.devnatan.katan.api.composition.CompositionStore
import me.devnatan.katan.api.composition.Compositions

class CompositionsImpl : Compositions {

    private val mutex = Any()
    private val registered: MutableMap<Composition.Key, CompositionStore<*>> = hashMapOf()

    override operator fun <T : CompositionOptions> get(key: Composition.Key): CompositionStore<T>? {
        return synchronized(mutex) {
            @Suppress("UNCHECKED_CAST")
            registered[key] as? CompositionStore<T>
        }
    }

    override operator fun set(key: Composition.Key, composition: CompositionStore<*>) {
        return synchronized(mutex) {
            registered[key] = composition
        }
    }

    override fun iterator(): Iterator<CompositionStore<*>> {
        return registered.values.iterator()
    }

}