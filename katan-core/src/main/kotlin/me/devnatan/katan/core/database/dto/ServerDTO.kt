package me.devnatan.katan.core.database.dto

data class ServerDTO(
    val id: Int,
    val name: String,
    val containerId: String,
    val game: String,
    val gameVersion: String?,
    val host: String,
    val port: Int,
    val holders: List<ServerHolderDTO>,
    val compositions: List<String>
)