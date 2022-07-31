package org.katan.service.account

import kotlinx.datetime.Clock
import org.katan.model.account.Account
import org.katan.model.security.Hash
import org.katan.service.account.repository.AccountsRepository
import org.katan.service.id.IdService

internal class AccountServerImpl(
    private val idService: IdService,
    private val accountsRepository: AccountsRepository,
    private val hash: Hash
) : AccountService {

    override suspend fun getAccount(id: Long): Account? {
        return accountsRepository.findById(id)
    }

    override suspend fun getAccount(username: String): Account? {
        return accountsRepository.findByUsername(username)
    }

    override suspend fun createAccount(
        username: String,
        password: String
    ): Account {
        val impl = AccountImpl(
            id = idService.generate(),
            username = username,
            createdAt = Clock.System.now(),
            hash = hash.hash(password.toCharArray())
        )
        accountsRepository.addAccount(impl)
        return impl
    }

    override suspend fun deleteAccount(id: Long) {
        return accountsRepository.deleteAccount(id)
    }
}
