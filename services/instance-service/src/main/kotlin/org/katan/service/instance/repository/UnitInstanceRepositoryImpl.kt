package org.katan.service.instance.repository

import org.jetbrains.exposed.dao.LongEntity
import org.jetbrains.exposed.dao.LongEntityClass
import org.jetbrains.exposed.dao.id.EntityID
import org.jetbrains.exposed.dao.id.LongIdTable
import org.jetbrains.exposed.sql.Database
import org.jetbrains.exposed.sql.SchemaUtils
import org.jetbrains.exposed.sql.transactions.experimental.newSuspendedTransaction
import org.jetbrains.exposed.sql.transactions.transaction
import org.katan.model.instance.UnitInstance

internal object UnitInstanceTable : LongIdTable("instances") {

    val imageUpdatePolicy = varchar("image_update_policy", length = 64)
    val containerId = varchar("container_id", length = 255).nullable()
    val blueprintId = long("blueprint_id")
    val host = varchar("host", length = 255).nullable()
    val port = short("port").nullable()
    val status = varchar("status", length = 255)
}

internal class UnitInstanceEntity(id: EntityID<Long>) : LongEntity(id), InstanceEntity {
    companion object : LongEntityClass<UnitInstanceEntity>(UnitInstanceTable)

    override var updatePolicy by UnitInstanceTable.imageUpdatePolicy
    override var containerId by UnitInstanceTable.containerId
    override var blueprintId by UnitInstanceTable.blueprintId
    override var host by UnitInstanceTable.host
    override var port by UnitInstanceTable.port
    override var status by UnitInstanceTable.status

    override fun getId() = id.value
}

internal class UnitInstanceRepositoryImpl(private val database: Database) : UnitInstanceRepository {

    init {
        transaction(db = database) {
            SchemaUtils.create(UnitInstanceTable)
        }
    }

    override suspend fun findById(id: Long): InstanceEntity? {
        return newSuspendedTransaction(db = database) {
            UnitInstanceEntity.findById(id)
        }
    }

    override suspend fun create(instance: UnitInstance) {
        return newSuspendedTransaction(db = database) {
            UnitInstanceEntity.new(instance.id) {
                updatePolicy = instance.updatePolicy.id
                containerId = instance.containerId
                blueprintId = instance.blueprintId.value
                host = instance.connection?.host
                port = instance.connection?.port
                status = instance.status.value
            }
        }
    }

    override suspend fun delete(id: Long) {
        newSuspendedTransaction(db = database) {
            UnitInstanceEntity.findById(id)?.delete()
        }
    }

    override suspend fun update(id: Long, update: InstanceEntity.() -> Unit): InstanceEntity? {
        return newSuspendedTransaction(db = database) {
            UnitInstanceEntity.findById(id)?.apply(update)
        }
    }
}
