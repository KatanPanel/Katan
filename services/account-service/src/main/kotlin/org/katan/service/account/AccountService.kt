package org.katan.service.account

import kotlinx.datetime.Clock
import org.katan.security.Hash
import org.katan.EventsDispatcher
import org.katan.model.Snowflake
import org.katan.model.account.Account
import org.katan.model.toSnowflake
import org.katan.service.account.repository.AccountEntity
import org.katan.service.account.repository.AccountsRepository
import org.katan.service.id.IdService

interface AccountService {

    suspend fun listAccounts(): List<Account>

    suspend fun getAccount(id: Snowflake): Account?

    suspend fun getAccount(username: String): Account?

    suspend fun getAccountAndHash(username: String): Pair<Account, String>?

    suspend fun createAccount(username: String, displayName: String?, email: String, password: String): Account

    suspend fun deleteAccount(id: Snowflake)
}

internal class AccountServiceImpl(
    private val idService: IdService,
    private val accountsRepository: AccountsRepository,
    private val hashAlgorithm: Hash,
    private val eventsDispatcher: EventsDispatcher
) : AccountService {

    override suspend fun listAccounts(): List<Account> {
        return accountsRepository.findAll().map { entity -> entity.toDomain() }
    }

    override suspend fun getAccount(id: Snowflake): Account? {
        return accountsRepository.findById(id.value)?.toDomain()
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
        displayName: String?,
        email: String,
        password: String
    ): Account {
        if (accountsRepository.existsByUsername(username)) {
            throw AccountConflictException()
        }

        val now = Clock.System.now()
        val account = Account(
            id = idService.generate(),
            displayName = displayName,
            username = username,
            email = email,
            createdAt = now,
            updatedAt = now,
            lastLoggedInAt = null,
            avatar = null
        )

        val hash = hashAlgorithm.hash(password.toCharArray())
        accountsRepository.addAccount(account, hash)
        eventsDispatcher.dispatch(AccountCreatedEvent(account.id))
        return account
    }

    override suspend fun deleteAccount(id: Snowflake) {
        accountsRepository.deleteAccount(id.value)
        eventsDispatcher.dispatch(AccountDeletedEvent(id))
    }

    private fun AccountEntity.toDomain(): Account = Account(
        id = id.value.toSnowflake(),
        email = email,
        displayName = displayName,
        username = username,
        createdAt = createdAt,
        updatedAt = updatedAt,
        lastLoggedInAt = lastLoggedInAt,
        avatar = avatar?.toSnowflake()
    )
}
