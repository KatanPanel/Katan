package org.katan.service.unit.instance.repository

import org.jetbrains.exposed.dao.LongEntity
import org.jetbrains.exposed.dao.LongEntityClass
import org.jetbrains.exposed.dao.id.EntityID
import org.jetbrains.exposed.dao.id.LongIdTable
import org.jetbrains.exposed.sql.transactions.experimental.newSuspendedTransaction
import org.katan.model.unit.UnitInstance
import org.katan.model.unit.UnitInstanceStatus

internal object UnitInstanceTable : LongIdTable() {

    val status = varchar("status", length = 64)
    val image = varchar("image", length = 255)
    val container = varchar("container_id", length = 255)

}

internal class UnitInstanceEntity(id: EntityID<Long>) : LongEntity(id) {
    companion object : LongEntityClass<UnitInstanceEntity>(UnitInstanceTable)

    var image by UnitInstanceTable.image
    var status by UnitInstanceTable.status
    var container by UnitInstanceTable.container

}

internal class UnitInstanceRepositorySqlImpl : UnitInstanceRepository {

    override suspend fun findById(id: Long): UnitInstance? {
        return newSuspendedTransaction {
            UnitInstanceEntity.findById(id)?.let {
                UnitInstance(id, it.image, UnitInstanceStatus.getByName(it.status), it.container)
            }
        }
    }

    override suspend fun create(instance: UnitInstance) {
        return newSuspendedTransaction {
            UnitInstanceEntity.new(instance.id) {
                image = instance.image
                status = instance.status.name
                container = instance.container
            }
        }
    }

    override suspend fun updateStatus(id: Long, status: UnitInstanceStatus) {
        newSuspendedTransaction {
            UnitInstanceEntity.findById(id)?.status = status.name
        }
    }

    override suspend fun delete(id: Long) {
        newSuspendedTransaction {
            UnitInstanceEntity.findById(id)?.delete()
        }
    }

}