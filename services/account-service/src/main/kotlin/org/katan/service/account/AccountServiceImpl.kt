package org.katan.service.account

import kotlinx.datetime.Clock
import org.katan.crypto.SaltedHash
import org.katan.model.account.Account
import org.katan.service.account.repository.AccountEntity
import org.katan.service.account.repository.AccountsRepository
import org.katan.service.id.IdService

internal class AccountServiceImpl(
    private val idService: IdService,
    private val accountsRepository: AccountsRepository,
    private val saltedHash: SaltedHash
) : AccountService {

    override suspend fun getAccount(id: Long): Account? {
        return accountsRepository.findById(id)?.toDomain()
    }

    override suspend fun getAccount(username: String): Account? {
        return accountsRepository.findByUsername(username)?.toDomain()
    }

    override suspend fun getAccountAndHash(username: String): Pair<Account, String>? {
        // TODO optimize it
        val account = accountsRepository.findByUsername(username)?.toDomain() ?: return null
        val hash = accountsRepository.findHashByUsername(username) ?: return null

        return account to hash
    }

    override suspend fun createAccount(
        username: String,
        email: String,
        password: String
    ): Account {
        if (accountsRepository.existsByUsername(username)) {
            throw AccountConflictException()
        }

        val now = Clock.System.now()
        val impl = AccountImpl(
            id = idService.generate(),
            displayName = null,
            username = username,
            email = email,
            createdAt = now,
            updatedAt = now,
            lastLoggedInAt = null,
            avatar = null
        )

        val hash = saltedHash.hash(password.toCharArray())

        accountsRepository.addAccount(impl, hash)
        return impl
    }

    override suspend fun deleteAccount(id: Long) {
        return accountsRepository.deleteAccount(id)
    }

    private fun AccountEntity.toDomain(): Account {
        return AccountImpl(
            id = id.value,
            email = email,
            displayName = displayName,
            username = username,
            createdAt = createdAt,
            updatedAt = updatedAt,
            lastLoggedInAt = lastLoggedInAt,
            avatar = avatar
        )
    }
}
