package me.devnatan.katan.core.database.jdbc.entity

import org.jetbrains.exposed.dao.IntEntity
import org.jetbrains.exposed.dao.IntEntityClass
import org.jetbrains.exposed.dao.id.EntityID
import org.jetbrains.exposed.dao.id.IntIdTable

internal object ServersTable : IntIdTable("katan_servers") {

    val name = varchar("name", 255)
    val containerId = varchar("container_id", 255)
    val gameType = varchar("game_type", 255)
    val gameVersion = varchar("game_version", 255).nullable()
    val host = varchar("host", 255)
    val port = integer("port")

}

class ServerEntity(id: EntityID<Int>) : IntEntity(id) {
    companion object : IntEntityClass<ServerEntity>(ServersTable)

    var name by ServersTable.name
    var containerId by ServersTable.containerId
    var gameType by ServersTable.gameType
    var gameVersion by ServersTable.gameVersion
    var host by ServersTable.host
    var port by ServersTable.port

    val holders by ServerHolderEntity referrersOn ServerHoldersTable.server
    val compositions by ServerCompositionEntity referrersOn ServerCompositionsTable.server

}