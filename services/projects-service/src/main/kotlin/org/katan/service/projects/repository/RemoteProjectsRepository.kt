package org.katan.service.projects.repository

import kotlinx.datetime.Instant
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

internal object ProjectsTable : LongIdTable("projects") {

    val name = varchar("name", length = 255).index()
    val createdAt = timestamp("created_at")
}

internal class ProjectEntityImpl(id: EntityID<Long>) : LongEntity(id), ProjectEntity {
    companion object : LongEntityClass<ProjectEntityImpl>(ProjectsTable)

    override val entityId: Long get() = id.value
    override var name: String by ProjectsTable.name
    override var createdAt: Instant by ProjectsTable.createdAt
}

internal class RemoteProjectsRepository(private val database: Database) : ProjectsRepository {

    init {
        transaction(db = database) {
            SchemaUtils.createMissingTablesAndColumns(ProjectsTable)
        }
    }

    override suspend fun list(): List<ProjectEntity> = newSuspendedTransaction(db = database) {
        ProjectEntityImpl.all().notForUpdate().toList()
    }

    override suspend fun findById(id: Snowflake): ProjectEntity? = newSuspendedTransaction(db = database) {
        ProjectEntityImpl.findById(id.value)
    }

    override suspend fun create(id: Snowflake, name: String, createdAt: Instant) {
        newSuspendedTransaction(db = database) {
            ProjectEntityImpl.new(id.value) {
                this.createdAt = createdAt
            }
        }
    }
}