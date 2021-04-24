package me.devnatan.katan.database.repository

import me.devnatan.katan.database.dto.server.ServerDTO

interface ServersRepository {

    suspend fun listServers(): List<ServerDTO>

    suspend fun insertServer(server: ServerDTO)

}