package me.devnatan.katan.core.database.jdbc.entity

import org.jetbrains.exposed.dao.IntEntity
import org.jetbrains.exposed.dao.IntEntityClass
import org.jetbrains.exposed.dao.id.EntityID
import org.jetbrains.exposed.dao.id.IntIdTable

internal object ServersTable : IntIdTable("katan_servers") {

    val name = varchar("name", 255)
    val containerId = varchar("container_id", 255)
    val target = varchar("target", 255)

}

class ServerEntity(id: EntityID<Int>) : IntEntity(id) {
    companion object : IntEntityClass<ServerEntity>(ServersTable)

    var name by ServersTable.name
    var containerId by ServersTable.containerId
    var target by ServersTable.target

    val holders by ServerHolderEntity referrersOn ServerHoldersTable.server
    val compositions by ServerCompositionEntity referrersOn ServerCompositionsTable.server

}