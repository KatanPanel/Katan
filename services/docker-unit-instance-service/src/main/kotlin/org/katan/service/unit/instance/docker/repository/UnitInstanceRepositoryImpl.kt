package org.katan.service.unit.instance.docker.repository

import org.jetbrains.exposed.dao.LongEntity
import org.jetbrains.exposed.dao.LongEntityClass
import org.jetbrains.exposed.dao.id.EntityID
import org.jetbrains.exposed.dao.id.LongIdTable
import org.jetbrains.exposed.sql.Database
import org.jetbrains.exposed.sql.SchemaUtils
import org.jetbrains.exposed.sql.transactions.experimental.newSuspendedTransaction
import org.jetbrains.exposed.sql.transactions.transaction
import org.katan.model.instance.UnitInstance
import org.katan.service.unit.instance.repository.InstanceEntity
import org.katan.service.unit.instance.repository.UnitInstanceRepository

internal object UnitInstanceTable : LongIdTable("unit_instances") {

    val imageUpdatePolicy = varchar("image_update_policy", length = 64)
    val containerId = varchar("container_id", length = 255).nullable()
    val host = varchar("host", length = 255).nullable()
    val port = short("port").nullable()
    val status = varchar("status", length = 255)
}

internal class UnitInstanceEntity(id: EntityID<Long>) : LongEntity(id), InstanceEntity {
    companion object : LongEntityClass<UnitInstanceEntity>(UnitInstanceTable)

    override var updatePolicy: String by UnitInstanceTable.imageUpdatePolicy
    override var containerId: String? by UnitInstanceTable.containerId
    override var host: String? by UnitInstanceTable.host
    override var port: Short? by UnitInstanceTable.port
    override var status: String by UnitInstanceTable.status

    override fun getId(): Long = id.value
}

internal class UnitInstanceRepositoryImpl(
    private val database: Database
) : UnitInstanceRepository {

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
                host = instance.connection?.host
                port = instance.connection?.port?.toShort()
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
