package me.devnatan.katan.core.sql

import org.jetbrains.exposed.dao.EntityID
import org.jetbrains.exposed.dao.IntEntity
import org.jetbrains.exposed.dao.IntEntityClass
import org.jetbrains.exposed.dao.IntIdTable

internal object ServersTable : IntIdTable("katan_servers") {

    val name        = varchar("name", 64)
    val containerId = varchar("containerId", 64)
    val port        = integer("port")

}

class ServerEntity(id: EntityID<Int>) : IntEntity(id) {
    companion object : IntEntityClass<ServerEntity>(ServersTable)

    var name        by ServersTable.name
    var containerId by ServersTable.containerId
    var port        by ServersTable.port
    val holders     by ServerHolderEntity referrersOn ServerHoldersTable.server

}