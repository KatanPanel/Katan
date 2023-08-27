package org.katan.service.account.repository

import org.katan.model.account.Account

internal interface AccountsRepository {

    suspend fun findAll(): List<AccountEntity>

    suspend fun findById(id: Long): AccountEntity?

    suspend fun findByUsername(username: String): AccountEntity?

    suspend fun findHashByUsername(username: String): String?

    suspend fun addAccount(account: Account, hash: String)

    suspend fun deleteAccount(accountId: Long)

    suspend fun existsByUsername(username: String): Boolean
}
