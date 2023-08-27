package org.katan.service.account.repository

import org.jetbrains.exposed.dao.LongEntity
import org.jetbrains.exposed.dao.LongEntityClass
import org.jetbrains.exposed.dao.id.EntityID
import org.jetbrains.exposed.dao.id.LongIdTable
import org.jetbrains.exposed.sql.Database
import org.jetbrains.exposed.sql.SchemaUtils
import org.jetbrains.exposed.sql.kotlin.datetime.timestamp
import org.jetbrains.exposed.sql.transactions.experimental.newSuspendedTransaction
import org.jetbrains.exposed.sql.transactions.transaction
import org.katan.model.account.Account

internal object AccountsTable : LongIdTable("accounts") {

    val username = varchar("username", length = 255)
    val email = varchar("email", length = 255)
    val displayName = varchar("display_name", length = 255).nullable()
    val hash = varchar("hash", length = 255)
    val createdAt = timestamp("created_at")
    val updatedAt = timestamp("updated_at")
    val lastLoggedInAt = timestamp("last_logged_in_at").nullable()
    val avatar = long("avatar").nullable()
}

internal class AccountEntity(id: EntityID<Long>) : LongEntity(id) {
    internal companion object : LongEntityClass<AccountEntity>(AccountsTable)

    var username by AccountsTable.username
    var email by AccountsTable.email
    var displayName by AccountsTable.displayName
    var hash by AccountsTable.hash
    var createdAt by AccountsTable.createdAt
    var updatedAt by AccountsTable.updatedAt
    var lastLoggedInAt by AccountsTable.lastLoggedInAt
    val avatar by AccountsTable.avatar
}

internal class AccountsRepositoryImpl(private val database: Database) : AccountsRepository {

    init {
        transaction(db = database) {
            SchemaUtils.create(AccountsTable)
        }
    }

    override suspend fun findAll(): List<AccountEntity> = newSuspendedTransaction(db = database) {
        AccountEntity.all().notForUpdate().toList()
    }

    override suspend fun findById(id: Long): AccountEntity? {
        return newSuspendedTransaction(db = database) {
            AccountEntity.findById(id)
        }
    }

    override suspend fun findByUsername(username: String): AccountEntity? {
        return newSuspendedTransaction(db = database) {
            AccountEntity.find {
                AccountsTable.username eq username
            }.firstOrNull()
        }
    }

    override suspend fun findHashByUsername(username: String): String? {
        return newSuspendedTransaction(db = database) {
            AccountEntity.find {
                AccountsTable.username eq username
            }.firstOrNull()?.hash
        }
    }

    override suspend fun addAccount(account: Account, hash: String) {
        newSuspendedTransaction(db = database) {
            AccountEntity.new(account.id.value) {
                this.username = account.username
                this.email = account.email
                this.hash = hash
                this.createdAt = account.createdAt
                this.updatedAt = account.updatedAt
                this.lastLoggedInAt = account.lastLoggedInAt
            }
        }
    }

    override suspend fun deleteAccount(accountId: Long) {
        newSuspendedTransaction(db = database) {
            AccountEntity.findById(accountId)?.delete()
        }
    }

    override suspend fun existsByUsername(username: String): Boolean {
        return newSuspendedTransaction(db = database) {
            !AccountEntity.find { AccountsTable.username eq username }.empty()
        }
    }
}
