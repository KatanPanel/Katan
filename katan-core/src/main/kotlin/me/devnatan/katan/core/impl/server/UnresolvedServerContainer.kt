package me.devnatan.katan.core.impl.server

import me.devnatan.katan.api.server.ServerContainer

class UnresolvedServerContainer(name: String) : ServerContainer("<unresolved>", name) {

    override suspend fun start() {
        throw UnsupportedOperationException("Unresolved container")
    }

    override suspend fun stop() {
        throw UnsupportedOperationException("Unresolved container")
    }

}