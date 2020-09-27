package me.devnatan.katan.core.server

import me.devnatan.katan.api.server.ServerContainer

class NoOpServerContainer(id: String) : ServerContainer(id) {

    override suspend fun start() {
    }

    override suspend fun stop() {
    }

}