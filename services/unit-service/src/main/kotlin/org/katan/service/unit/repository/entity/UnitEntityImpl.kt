package org.katan.service.unit.repository.entity

import org.jetbrains.exposed.dao.LongEntity
import org.jetbrains.exposed.dao.LongEntityClass
import org.jetbrains.exposed.dao.id.EntityID
import org.jetbrains.exposed.dao.id.LongIdTable
import org.jetbrains.exposed.sql.kotlin.datetime.timestamp
import org.katan.service.unit.repository.UnitEntity

internal object UnitTable : LongIdTable("units") {

    val externalId = varchar("ext_id", length = 255).nullable()
    val nodeId = integer("node_id")
    val name = varchar("name", length = 255)
    val createdAt = timestamp("created_at")
    val updatedAt = timestamp("updated_at")
    val deletedAt = timestamp("deleted_at").nullable()
    val instanceId = long("instance_id").nullable()
    val status = varchar("status", length = 255)
}

internal class UnitEntityImpl(id: EntityID<Long>) : LongEntity(id), UnitEntity {
    companion object : LongEntityClass<UnitEntityImpl>(UnitTable)

    override var externalId by UnitTable.externalId
    override var nodeId by UnitTable.nodeId
    override var name by UnitTable.name
    override var createdAt by UnitTable.createdAt
    override var updatedAt by UnitTable.updatedAt
    override var deletedAt by UnitTable.deletedAt
    override var instanceId by UnitTable.instanceId
    override var status by UnitTable.status

    override fun getId(): Long = id.value
}
