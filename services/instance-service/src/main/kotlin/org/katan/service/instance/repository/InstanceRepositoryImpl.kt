package org.katan.service.instance.repository

import org.jetbrains.exposed.dao.LongEntity
import org.jetbrains.exposed.dao.LongEntityClass
import org.jetbrains.exposed.dao.id.EntityID
import org.jetbrains.exposed.dao.id.LongIdTable
import org.jetbrains.exposed.sql.Database
import org.jetbrains.exposed.sql.SchemaUtils
import org.jetbrains.exposed.sql.kotlin.datetime.timestamp
import org.jetbrains.exposed.sql.transactions.experimental.newSuspendedTransaction
import org.jetbrains.exposed.sql.transactions.transaction
import org.katan.model.Snowflake
import org.katan.model.instance.UnitInstance

internal object UnitInstanceTable : LongIdTable("instances") {

    val imageUpdatePolicy = varchar("image_update_policy", length = 64)
    val containerId = varchar("cid", length = 255).nullable()
    val blueprintId = long("bid")
    val host = varchar("host", length = 255).nullable()
    val port = short("port").nullable()
    val status = varchar("status", length = 255)
    val createdAt = timestamp("created_at")
}

internal class UnitInstanceEntity(id: EntityID<Long>) : LongEntity(id), InstanceEntity {
    companion object : LongEntityClass<UnitInstanceEntity>(UnitInstanceTable)

    override var updatePolicy by UnitInstanceTable.imageUpdatePolicy
    override var containerId by UnitInstanceTable.containerId
    override var blueprintId by UnitInstanceTable.blueprintId
    override var host by UnitInstanceTable.host
    override var port by UnitInstanceTable.port
    override var status by UnitInstanceTable.status
    override var createdAt by UnitInstanceTable.createdAt

    override fun getId(): Long = id.value
}

internal class InstanceRepositoryImpl(private val database: Database) : InstanceRepository {

    init {
        transaction(db = database) {
            SchemaUtils.create(UnitInstanceTable)
        }
    }

    override suspend fun findById(id: Snowflake): InstanceEntity? =
        newSuspendedTransaction(db = database) {
            UnitInstanceEntity.findById(id.value)
        }

    override suspend fun create(instance: UnitInstance) {
        newSuspendedTransaction(db = database) {
            UnitInstanceEntity.new(instance.id.value) {
                updatePolicy = instance.updatePolicy.id
                containerId = instance.containerId
                blueprintId = instance.blueprintId.value
                host = instance.connection?.host
                port = instance.connection?.port
                status = instance.status.value
            }
        }
    }

    override suspend fun delete(id: Snowflake) {
        newSuspendedTransaction(db = database) {
            UnitInstanceEntity.findById(id.value)?.delete()
        }
    }

    override suspend fun update(id: Snowflake, update: InstanceEntity.() -> Unit): InstanceEntity? =
        newSuspendedTransaction(db = database) {
            UnitInstanceEntity.findById(id.value)?.apply(update)
        }
}
