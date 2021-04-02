package me.devnatan.katan.core.repository

import kotlinx.coroutines.Dispatchers
import me.devnatan.katan.api.security.account.Account
import me.devnatan.katan.common.impl.account.SecureAccount
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

class JDBCAccountsRepository(private val connector: JDBCConnector) :
    AccountsRepository {

    override suspend fun listAccounts(): List<Account> {
        return newSuspendedTransaction(Dispatchers.IO, connector.database) {
            AccountEntity.all().map { entity ->
                SecureAccount(
                    entity.id.value,
                    entity.username,
                    entity.registeredAt
                ).apply {
                    lastLogin = entity.lastLogin
                    if (entity.password != null)
                        password = entity.password!!
                }
            }
        }
    }

    override suspend fun insertAccount(account: Account) {
        newSuspendedTransaction(Dispatchers.IO, connector.database) {
            AccountEntity.new(account.id) {
                this.username = account.username
                this.registeredAt = account.registeredAt
                this.lastLogin = account.lastLogin
                if (account is SecureAccount)
                    this.password = account.password
            }
        }
    }

    override suspend fun updateAccount(account: Account) {
        newSuspendedTransaction(Dispatchers.IO, connector.database) {
            AccountsTable.update({ AccountsTable.id eq account.id }) {
                it[username] = account.username
                it[lastLogin] = account.lastLogin
                if (account is SecureAccount && account.password.orEmpty()
                        .isNotEmpty()
                )
                    it[password] = account.password
            }
        }
    }

}