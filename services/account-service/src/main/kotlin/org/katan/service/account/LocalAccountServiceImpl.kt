package org.katan.service.account

import kotlinx.datetime.Clock
import org.katan.model.account.Account
import org.katan.model.security.Hash
import org.katan.service.id.IdService

internal class LocalAccountServiceImpl(
    private val idService: IdService,
    private val hash: Hash
) : AccountService {

    override suspend fun getAccount(id: Long): Account? {
        TODO("Not yet implemented")
    }

    override suspend fun getAccount(username: String): Account? {
        TODO("Not yet implemented")
    }

    override suspend fun createAccount(
        username: String,
        password: String,
    ): Account {
        return AccountImpl(
            id = idService.generate(),
            username = username,
            createdAt = Clock.System.now(),
            hash = hash.hash(password.toCharArray())
        )
    }

    override suspend fun activateAccount(id: Long): Account {
        TODO("Not yet implemented")
    }

    override suspend fun deactivateAccount(id: Long): Account {
        TODO("Not yet implemented")
    }

}