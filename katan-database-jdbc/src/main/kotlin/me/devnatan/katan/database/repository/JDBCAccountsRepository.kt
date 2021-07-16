package me.devnatan.katan.database.repository

import kotlinx.coroutines.Dispatchers
import me.devnatan.katan.api.security.HashedPasswordCredentials
import me.devnatan.katan.api.security.account.Account
import me.devnatan.katan.api.security.account.AccountFactory
import me.devnatan.katan.api.security.isEmpty
import me.devnatan.katan.database.DatabaseService
import me.devnatan.katan.database.entity.JDBCAccountEntity
import me.devnatan.katan.database.entity.JDBCAccountsTable
import me.devnatan.katan.database.transaction
import org.jetbrains.exposed.sql.SchemaUtils
import org.jetbrains.exposed.sql.Transaction
import org.jetbrains.exposed.sql.update

class JDBCAccountsRepository(
    private val database: DatabaseService<Transaction>,
    private val accountFactory: AccountFactory
) : AccountsRepository {

    override suspend fun init() {
        database.transaction(Dispatchers.Unconfined) {
            SchemaUtils.create(JDBCAccountsTable)
        }
    }

    override suspend fun findAll(): Collection<Account> {
        return database.transaction {
            JDBCAccountEntity.all().map { entity ->
                accountFactory.create(entity.id.value, entity.username, entity.registeredAt).apply {
                    val password = entity.password
                    if (password != null)
                        credentials = HashedPasswordCredentials(password)

                    lastLogin = entity.lastLogin
                }
            }
        }
    }

    override suspend fun insert(account: Account) {
        database.transaction {
            JDBCAccountEntity.new(account.id) {
                this.username = account.username
                this.registeredAt = account.registeredAt
                this.lastLogin = account.lastLogin

                if (!account.credentials.isEmpty())
                    this.password = (account.credentials as HashedPasswordCredentials).hashedValue
            }
        }
    }

    override suspend fun update(account: Account) {
        database.transaction {
            JDBCAccountsTable.update({ JDBCAccountsTable.id eq account.id }) {
                it[username] = account.username
                it[lastLogin] = account.lastLogin

                if (!account.credentials.isEmpty())
                    it[password] = (account.credentials as HashedPasswordCredentials).hashedValue
            }
        }
    }

}