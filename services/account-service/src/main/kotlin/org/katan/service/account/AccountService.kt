package org.katan.service.account

import org.katan.model.account.Account

public interface AccountService {

    public suspend fun getAccount(id: Long): Account?

    public suspend fun getAccount(username: String): Account?

    public suspend fun getAccountAndHash(username: String): Pair<Account, String>?

    public suspend fun createAccount(username: String, password: String): Account

    public suspend fun deleteAccount(id: Long)
}
