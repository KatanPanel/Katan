package org.katan.service.unit.instance.repository

import org.jetbrains.exposed.dao.LongEntity
import org.jetbrains.exposed.dao.LongEntityClass
import org.jetbrains.exposed.dao.id.EntityID
import org.jetbrains.exposed.dao.id.LongIdTable
import org.jetbrains.exposed.sql.transactions.experimental.newSuspendedTransaction
import org.katan.model.unit.UnitInstance

internal object UnitInstanceTable : LongIdTable() {

    val image = varchar("image", length = 255)
    val runtimeId = varchar("runtime_id", length = 255)

}

internal class UnitInstanceEntity(id: EntityID<Long>) : LongEntity(id) {
    companion object : LongEntityClass<UnitInstanceEntity>(UnitInstanceTable)

    var image by UnitInstanceTable.image
    var runtimeId by UnitInstanceTable.runtimeId

}

internal class UnitInstanceRepositorySqlImpl : UnitInstanceRepository {

    override suspend fun findById(id: Long): UnitInstance? {
        return newSuspendedTransaction {
            UnitInstanceEntity.findById(id)?.let {
                UnitInstance(id, it.image, it.runtimeId)
            }
        }
    }

    override suspend fun create(instance: UnitInstance) {
        return newSuspendedTransaction {
            UnitInstanceEntity.new(instance.id) {
                image = instance.image
                runtimeId = instance.runtimeId
            }
        }
    }

    override suspend fun delete(id: Long) {
        newSuspendedTransaction {
            UnitInstanceEntity.findById(id)?.delete()
        }
    }

}