package org.katan.service.server.repository.entity

import org.jetbrains.exposed.dao.LongEntity
import org.jetbrains.exposed.dao.LongEntityClass
import org.jetbrains.exposed.dao.id.EntityID
import org.jetbrains.exposed.dao.id.LongIdTable
import org.jetbrains.exposed.sql.kotlin.datetime.timestamp

internal object UnitTable : LongIdTable("units") {

    val externalId = varchar("ext_id", length = 255).nullable()
    val nodeId = integer("node_id")
    val name = varchar("name", length = 255)
    val createdAt = timestamp("created_at")
    val updatedAt = timestamp("updated_at")
    val deletedAt = timestamp("deleted_at").nullable()
    val instanceId = long("instance_id")

}

internal class UnitEntity(id: EntityID<Long>) : LongEntity(id) {
    companion object : LongEntityClass<UnitEntity>(UnitTable)

    var externalId by UnitTable.externalId
    var nodeId by UnitTable.nodeId
    var name by UnitTable.name
    var createdAt by UnitTable.createdAt
    var updatedAt by UnitTable.updatedAt
    var deletedAt by UnitTable.deletedAt
    var instanceId by UnitTable.instanceId

}