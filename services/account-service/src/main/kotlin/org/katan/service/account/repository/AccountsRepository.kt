package org.katan.service.account.repository

import org.katan.model.account.Account

internal interface AccountsRepository {

    suspend fun findById(id: Long): Account?

    suspend fun addAccount(account: Account)

    suspend fun deleteAccount(accountId: Long)

}