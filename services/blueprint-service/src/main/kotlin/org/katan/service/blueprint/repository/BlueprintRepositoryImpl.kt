package org.katan.service.blueprint.repository

import kotlinx.datetime.Instant
import org.jetbrains.exposed.dao.LongEntity
import org.jetbrains.exposed.dao.LongEntityClass
import org.jetbrains.exposed.dao.id.EntityID
import org.jetbrains.exposed.dao.id.LongIdTable
import org.jetbrains.exposed.sql.Database
import org.jetbrains.exposed.sql.SchemaUtils
import org.jetbrains.exposed.sql.kotlin.datetime.timestamp
import org.jetbrains.exposed.sql.statements.api.ExposedBlob
import org.jetbrains.exposed.sql.transactions.experimental.newSuspendedTransaction
import org.jetbrains.exposed.sql.transactions.transaction
import org.katan.model.Snowflake
import org.katan.model.toSnowflake

internal object BlueprintTable : LongIdTable("blueprints") {

    val createdAt = timestamp("created_at")
    val updatedAt = timestamp("updated_at")
    val content = blob("content")
}

internal class BlueprintEntityImpl(id: EntityID<Long>) : LongEntity(id), BlueprintEntity {
    companion object : LongEntityClass<BlueprintEntityImpl>(BlueprintTable)

    override var createdAt: Instant by BlueprintTable.createdAt
    override var updatedAt: Instant by BlueprintTable.updatedAt
    override var content: ExposedBlob by BlueprintTable.content

    override fun getId(): Snowflake = id.value.toSnowflake()
}

internal class BlueprintRepositoryImpl(private val database: Database) : BlueprintRepository {

    init {
        transaction(db = database) {
            SchemaUtils.createMissingTablesAndColumns(BlueprintTable)
        }
    }

    override suspend fun findAll(): List<BlueprintEntity> {
        return newSuspendedTransaction(db = database) {
            BlueprintEntityImpl.all().notForUpdate().toList()
        }
    }

    override suspend fun find(id: Long): BlueprintEntity? = newSuspendedTransaction(db = database) {
        BlueprintEntityImpl.findById(id)
    }

    override suspend fun create(id: Long, spec: ByteArray, createdAt: Instant) {
        newSuspendedTransaction(db = database) {
            BlueprintEntityImpl.new(id) {
                content = ExposedBlob(spec)
                this.createdAt = createdAt
                this.updatedAt = createdAt
            }
        }
    }

    override suspend fun delete(id: Long) {
        newSuspendedTransaction(db = database) {
            BlueprintEntityImpl.findById(id)?.delete()
        }
    }
}
