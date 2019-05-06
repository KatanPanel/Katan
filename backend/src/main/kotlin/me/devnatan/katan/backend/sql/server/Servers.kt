package me.devnatan.katan.backend.sql.server

import org.jetbrains.exposed.dao.EntityID
import org.jetbrains.exposed.dao.IntEntity
import org.jetbrains.exposed.dao.IntEntityClass
import org.jetbrains.exposed.dao.IntIdTable

object Servers : IntIdTable("katan_servers") {

    val serverId = integer("serverId").primaryKey()
    val name = varchar("name", 50).uniqueIndex()
    val address = varchar("address", 50)
    val port = integer("port")
    val pathRoot = varchar("path_root", 255)
    val jarFile = varchar("jar_file", 255)
    val initParams = varchar("init_params", 255)

}

class ServerEntity(id: EntityID<Int>) : IntEntity(id) {
    companion object : IntEntityClass<ServerEntity>(Servers)

    var serverId    by Servers.serverId
    var name        by Servers.name
    var address     by Servers.address
    var port        by Servers.port
    var pathRoot    by Servers.pathRoot
    var jarFile     by Servers.jarFile
    var initParams  by Servers.initParams

}