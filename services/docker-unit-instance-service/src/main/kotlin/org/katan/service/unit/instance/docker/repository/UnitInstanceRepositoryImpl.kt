package org.katan.service.unit.instance.docker.repository

import org.jetbrains.exposed.dao.LongEntity
import org.jetbrains.exposed.dao.LongEntityClass
import org.jetbrains.exposed.dao.id.EntityID
import org.jetbrains.exposed.dao.id.LongIdTable
import org.jetbrains.exposed.sql.Database
import org.jetbrains.exposed.sql.SchemaUtils
import org.jetbrains.exposed.sql.transactions.experimental.newSuspendedTransaction
import org.jetbrains.exposed.sql.transactions.transaction
import org.katan.model.unit.UnitInstance
import org.katan.model.unit.UnitInstanceStatus
import org.katan.service.unit.instance.docker.model.DockerUnitInstanceImpl
import org.katan.service.unit.instance.repository.UnitInstanceRepository

internal object UnitInstanceTable : LongIdTable() {

    val imageId = varchar("image_id", length = 255)
    val containerId = varchar("container_id", length = 255)
}

internal class UnitInstanceEntity(id: EntityID<Long>) : LongEntity(id) {
    companion object : LongEntityClass<UnitInstanceEntity>(UnitInstanceTable)

    var imageId by UnitInstanceTable.imageId
    var containerId by UnitInstanceTable.containerId
}

internal class UnitInstanceRepositoryImpl(
    private val database: Database
) : UnitInstanceRepository {

    init {
        transaction(db = database) {
            SchemaUtils.create(UnitInstanceTable)
        }
    }

    override suspend fun findById(id: Long): UnitInstance? {
        return newSuspendedTransaction(db = database) {
            UnitInstanceEntity.findById(id)?.let { entity ->
                DockerUnitInstanceImpl(
                    id,
                    UnitInstanceStatus.None,
                    entity.imageId,
                    entity.containerId
                )
            }
        }
    }

    override suspend fun create(instance: UnitInstance) {
        require(instance is DockerUnitInstanceImpl)
        return newSuspendedTransaction(db = database) {
            UnitInstanceEntity.new(instance.id) {
                imageId = instance.imageId
                containerId = instance.containerId
            }
        }
    }

    override suspend fun delete(id: Long) {
        newSuspendedTransaction(db = database) {
            UnitInstanceEntity.findById(id)?.delete()
        }
    }
}
