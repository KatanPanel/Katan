package org.katan.service.account

import kotlinx.datetime.Clock
import org.katan.model.account.Account
import org.katan.service.id.IdService

internal class AccountServiceImpl(
    private val idService: IdService
) : AccountService {

    override suspend fun getAccount(id: Long): Account? {
        TODO("Not yet implemented")
    }

    override suspend fun createAccount(username: String): Account {
        return AccountImpl(
            id = idService.generate(),
            username = username,
            registeredAt = Clock.System.now()
        )
    }

    override suspend fun activateAccount(id: Long): Account {
        TODO("Not yet implemented")
    }

    override suspend fun deactivateAccount(id: Long): Account {
        TODO("Not yet implemented")
    }

}