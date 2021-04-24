package me.devnatan.katan.database.repository

import me.devnatan.katan.database.dto.account.AccountDTO

interface AccountsRepository {

    suspend fun listAccounts(): List<AccountDTO>

    suspend fun insertAccount(account: AccountDTO)

    suspend fun updateAccount(account: AccountDTO)

}