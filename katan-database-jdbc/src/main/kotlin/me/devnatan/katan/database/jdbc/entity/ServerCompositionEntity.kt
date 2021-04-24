package me.devnatan.katan.database.jdbc.entity

import org.jetbrains.exposed.dao.IntEntity
import org.jetbrains.exposed.dao.IntEntityClass
import org.jetbrains.exposed.dao.id.EntityID
import org.jetbrains.exposed.dao.id.IntIdTable

internal object ServerCompositionsTable : IntIdTable("katan_server_compositions") {

    val key = varchar("name", 255)
    val server = reference("server", ServersTable)

}

class ServerCompositionEntity(id: EntityID<Int>) : IntEntity(id) {
    companion object : IntEntityClass<ServerCompositionEntity>(ServerCompositionsTable)

    var key by ServerCompositionsTable.key
    var server by ServerCompositionsTable.server

}