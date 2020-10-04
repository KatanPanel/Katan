package me.devnatan.katan.core.repository

import me.devnatan.katan.api.account.Account
import me.devnatan.katan.common.account.SecureAccount
import me.devnatan.katan.core.KatanCore
import me.devnatan.katan.core.database.jdbc.JDBCConnector
import me.devnatan.katan.core.database.jdbc.entity.AccountEntity
import me.devnatan.katan.core.database.jdbc.entity.AccountsTable
import org.jetbrains.exposed.sql.transactions.experimental.newSuspendedTransaction
import org.jetbrains.exposed.sql.update

interface AccountsRepository {

    suspend fun listAccounts(): List<Account>

    suspend fun insertAccount(account: Account)

    suspend fun updateAccount(account: Account)

}

class JDBCAccountsRepository(private val core: KatanCore, private val connector: JDBCConnector) : AccountsRepository {

    override suspend fun listAccounts(): List<Account> {
        return newSuspendedTransaction(db = connector.database) {
            AccountEntity.all().map { entity ->
                SecureAccount(
                    entity.id.value,
                    entity.username,
                    entity.registeredAt
                ).apply {
                    if (entity.password != null)
                        password = entity.password!!
                }
            }
        }
    }

    override suspend fun insertAccount(account: Account) {
        newSuspendedTransaction(db = connector.database) {
            AccountEntity.new(account.id) {
                this.username = account.username
                this.registeredAt = account.registeredAt
                if (account is SecureAccount)
                    this.password = account.password
            }
        }
    }

    override suspend fun updateAccount(account: Account) {
        newSuspendedTransaction(db = (core.database as JDBCConnector).database) {
            AccountsTable.update({ AccountsTable.id eq account.id }) {
                it[username] = account.username
                if (account is SecureAccount && account.password.isNotEmpty())
                    it[password] = account.password
            }
        }
    }

}