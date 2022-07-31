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
import org.katan.service.account.AccountImpl

private object AccountsTable : LongIdTable() {

    val username = varchar("username", length = 255)
    val hash = varchar("hash", length = 255)
    val createdAt = timestamp("created_at")
    val updatedAt = timestamp("updated_at")
    val lastLoggedInAt = timestamp("last_logged_in_at").nullable()

}

private class AccountsEntity(id: EntityID<Long>): LongEntity(id) {
    companion object : LongEntityClass<AccountsEntity>(AccountsTable)

    var username by AccountsTable.username
    var hash by AccountsTable.hash
    var createdAt by AccountsTable.createdAt
    var updatedAt by AccountsTable.updatedAt
    var lastLoggedInAt by AccountsTable.lastLoggedInAt

}

internal class AccountsRepositoryImpl(
    private val database: Database
) : AccountsRepository {

    init {
        transaction(db = database) {
            SchemaUtils.create(AccountsTable)
        }
    }

    override suspend fun findById(id: Long): Account? {
        return newSuspendedTransaction(db = database) {
            AccountsEntity.findById(id)?.let {
                AccountImpl(
                    it.id.value,
                    it.username,
                    it.hash,
                    it.createdAt,
                    it.updatedAt,
                    it.lastLoggedInAt
                )
            }
        }
    }

    override suspend fun findByUsername(username: String): Account? {
        return newSuspendedTransaction(db = database) {
            AccountsEntity.find { AccountsTable.username eq username }.firstOrNull()?.let {
                AccountImpl(
                    it.id.value,
                    it.username,
                    it.hash,
                    it.createdAt,
                    it.updatedAt,
                    it.lastLoggedInAt
                )
            }
        }
    }

    override suspend fun addAccount(account: Account) {
        newSuspendedTransaction(db = database) {
            AccountsEntity.new(account.id) {
                username = account.username
                hash = account.hash
                createdAt = account.createdAt
                updatedAt = account.updatedAt
                lastLoggedInAt = account.lastLoggedInAt
            }
        }
    }

    override suspend fun deleteAccount(accountId: Long) {
        newSuspendedTransaction(db = database) {
            AccountsEntity.findById(accountId)?.delete()
        }
    }

}