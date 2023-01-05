package org.katan.service.account

import org.katan.model.account.Account

interface AccountService {

    suspend fun getAccount(id: Long): Account?

    suspend fun getAccount(username: String): Account?

    suspend fun getAccountAndHash(username: String): Pair<Account, String>?

    suspend fun createAccount(username: String, email: String, password: String): Account

    suspend fun deleteAccount(id: Long)
}
