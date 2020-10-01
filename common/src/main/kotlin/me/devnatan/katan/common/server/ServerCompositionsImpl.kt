package me.devnatan.katan.common.server

import me.devnatan.katan.api.server.ServerComposition
import me.devnatan.katan.api.server.ServerCompositions

class ServerCompositionsImpl : ServerCompositions {

    private val lock = Any()
    private val registered = arrayListOf<ServerComposition<*>>()

    override operator fun <T : ServerComposition<*>> get(key: ServerComposition.Key<T>): T? {
        return synchronized(lock) {
            registered.firstOrNull { it.key == key } as? T
        }
    }

    override fun add(composition: ServerComposition<*>) {
        return synchronized(lock) {
            registered.add(composition)
        }
    }

}