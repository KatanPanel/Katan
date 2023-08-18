package org.katan.service.account

import kotlinx.datetime.Clock
import org.katan.crypto.SaltedHash
import org.katan.event.EventScope
import org.katan.model.Snowflake
import org.katan.model.account.Account
import org.katan.service.account.repository.AccountEntity
import org.katan.service.account.repository.AccountsRepository
import org.katan.service.id.IdService

interface AccountService {

    suspend fun getAccount(id: Snowflake): Account?

    suspend fun getAccount(username: String): Account?

    suspend fun getAccountAndHash(username: String): Pair<Account, String>?

    suspend fun createAccount(username: String, displayName: String, email: String, password: String): Account

    suspend fun deleteAccount(id: Snowflake)
}

internal class AccountServiceImpl(
    private val idService: IdService,
    private val accountsRepository: AccountsRepository,
    private val saltedHash: SaltedHash,
    private val eventsDispatcher: EventScope,
) : AccountService {

    override suspend fun getAccount(id: Snowflake): Account? {
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
        displayName: String,
        email: String,
        password: String
    ): Account {
        if (accountsRepository.existsByUsername(username)) {
            throw AccountConflictException()
        }

        val now = Clock.System.now()
        val account = AccountImpl(
            id = idService.generate(),
            displayName = displayName,
            username = username,
            email = email,
            createdAt = now,
            updatedAt = now,
            lastLoggedInAt = null,
            avatar = null
        )

        val hash = saltedHash.hash(password.toCharArray())
        accountsRepository.addAccount(account, hash)
        eventsDispatcher.dispatch(AccountCreatedEvent(account.id))
        return account
    }

    override suspend fun deleteAccount(id: Snowflake) {
        accountsRepository.deleteAccount(id)
        eventsDispatcher.dispatch(AccountDeletedEvent(id))
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
