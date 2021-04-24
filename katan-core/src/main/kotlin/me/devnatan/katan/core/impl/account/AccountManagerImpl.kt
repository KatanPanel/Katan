package me.devnatan.katan.core.impl.account

import me.devnatan.katan.api.event.account.AccountCreateEvent
import me.devnatan.katan.api.event.account.AccountLoginEvent
import me.devnatan.katan.api.event.account.AccountRegisterEvent
import me.devnatan.katan.api.event.account.AccountUpdateEvent
import me.devnatan.katan.api.security.Credentials
import me.devnatan.katan.api.security.PasswordCredentials
import me.devnatan.katan.api.security.account.Account
import me.devnatan.katan.api.security.account.AccountManager
import me.devnatan.katan.common.impl.account.SecureAccount
import me.devnatan.katan.core.KatanCore
import me.devnatan.katan.database.dto.account.AccountDTO
import me.devnatan.katan.database.repository.AccountsRepository
import java.time.Instant
import java.util.*

class AccountManagerImpl(
    private val core: KatanCore,
    private val repository: AccountsRepository
) : AccountManager {

    private val accounts: MutableSet<Account> = hashSetOf()

    internal suspend fun loadAccounts() {
        accounts.addAll(repository.listAccounts().map { dto ->
            SecureAccount(dto.id, dto.username, dto.registeredAt).apply {
                lastLogin = dto.lastLogin
                password = dto.password
            }
        })
    }

    override fun getAccounts(): List<Account> {
        return accounts.toList()
    }

    override suspend fun getAccount(username: String): Account? {
        return accounts.find { it.username == username }
    }

    override suspend fun getAccount(id: UUID): Account? {
        return accounts.find { it.id == id }
    }

    override fun createAccount(username: String, password: String): Account {
        val account =
            SecureAccount(UUID.randomUUID(), username, Instant.now()).apply {
                this.password = if (password.isNotBlank())
                    core.hash.hash(password.toCharArray())
                else password
            }

        synchronized(accounts) {
            if (!accounts.add(account))
                throw IllegalArgumentException(username)
        }

        core.eventBus.publish(AccountCreateEvent(account))
        return account
    }

    private fun generateAccountDto(account: Account): AccountDTO {
        return AccountDTO(account.id,
            account.username,
            account.registeredAt,
            account.lastLogin,
            if (account is SecureAccount) account.password else null
        )
    }

    override suspend fun registerAccount(account: Account) {
        repository.insertAccount(generateAccountDto(account))
        core.eventBus.publish(AccountRegisterEvent(account))
    }

    private suspend fun updateAccount(account: Account) {
        repository.updateAccount(generateAccountDto(account))
        core.eventBus.publish(AccountUpdateEvent(account))
    }

    override fun existsAccount(username: String): Boolean {
        return accounts.any { it.username == username }
    }

    override suspend fun authenticate(
        account: Account,
        credentials: Credentials
    ): Boolean {
        check(account is SecureAccount)
        check(credentials is PasswordCredentials)

        if (credentials.validate(
                account.password.orEmpty(),
                core.hash
            )
        ) {
            val now = Instant.now()
            core.eventBus.publish(AccountLoginEvent(account, now))
            account.lastLogin = now
            updateAccount(account)
            return true
        }

        return false
    }

}