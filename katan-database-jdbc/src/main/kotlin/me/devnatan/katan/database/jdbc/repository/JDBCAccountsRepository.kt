package me.devnatan.katan.database.jdbc.repository

import kotlinx.coroutines.Dispatchers
import me.devnatan.katan.database.dto.account.AccountDTO
import me.devnatan.katan.database.jdbc.JDBCConnector
import me.devnatan.katan.database.jdbc.entity.AccountEntity
import me.devnatan.katan.database.jdbc.entity.AccountsTable
import me.devnatan.katan.database.repository.AccountsRepository
import org.jetbrains.exposed.sql.transactions.experimental.newSuspendedTransaction
import org.jetbrains.exposed.sql.update

internal class JDBCAccountsRepository(private val connector: JDBCConnector) :
    AccountsRepository {

    override suspend fun listAccounts(): List<AccountDTO> = connector.wrap("accounts.list") {
        newSuspendedTransaction(Dispatchers.IO, connector.database) {
            AccountEntity.all().map { entity ->
                AccountDTO(
                    entity.id.value,
                    entity.username,
                    entity.registeredAt,
                    entity.lastLogin,
                    entity.password
                )
            }
        }
    }

    override suspend fun insertAccount(account: AccountDTO) = connector.wrap<Unit>("accounts.insert") {
        newSuspendedTransaction(Dispatchers.IO, connector.database) {
            AccountEntity.new(account.id) {
                this.username = account.username
                this.registeredAt = account.registeredAt
                this.lastLogin = account.lastLogin
                this.password = account.password
            }
        }
    }

    override suspend fun updateAccount(account: AccountDTO) = connector.wrap<Unit>("accounts.update") {
        newSuspendedTransaction(Dispatchers.IO, connector.database) {
            AccountsTable.update({ AccountsTable.id eq account.id }) {
                it[username] = account.username
                it[lastLogin] = account.lastLogin
                it[password] = account.password
            }
        }
    }

}