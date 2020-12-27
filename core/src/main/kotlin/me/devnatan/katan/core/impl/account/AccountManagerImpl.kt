package me.devnatan.katan.core.impl.account

import me.devnatan.katan.api.account.Account
import me.devnatan.katan.api.account.AccountManager
import me.devnatan.katan.api.event.AccountCreateEvent
import me.devnatan.katan.api.event.AccountLoginEvent
import me.devnatan.katan.api.event.AccountRegisterEvent
import me.devnatan.katan.api.event.AccountUpdateEvent
import me.devnatan.katan.api.security.credentials.Credentials
import me.devnatan.katan.api.security.credentials.PasswordCredentials
import me.devnatan.katan.common.impl.account.SecureAccount
import me.devnatan.katan.core.KatanCore
import me.devnatan.katan.core.repository.AccountsRepository
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import java.time.Instant
import java.util.*

class AccountManagerImpl(
    private val core: KatanCore,
    private val repository: AccountsRepository
) : AccountManager {

    companion object {

        private val logger: Logger = LoggerFactory.getLogger(AccountManager::class.java)

    }

    private val accounts = hashSetOf<Account>()

    internal suspend fun loadAccounts() {
        accounts.addAll(repository.listAccounts())
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
        val account = SecureAccount(UUID.randomUUID(), username, Instant.now()).apply {
            this.password = if (password.isNotBlank())
                core.hash.hash(password.toCharArray())
            else password
        }

        synchronized(accounts) {
            if (!accounts.add(account))
                throw IllegalArgumentException(username)
        }

        core.eventBus.publish(AccountCreateEvent(account))
        logger.debug("Account ${account.id} created.")
        return account
    }

    override suspend fun registerAccount(account: Account) {
        repository.insertAccount(account)
        core.eventBus.publish(AccountRegisterEvent(account))
        logger.debug("Account ${account.id} registered.")
    }

    private suspend fun updateAccount(account: Account) {
        repository.updateAccount(account)
        core.eventBus.publish(AccountUpdateEvent(account))
        logger.debug("Account ${account.id} updated.")
    }

    override fun existsAccount(username: String): Boolean {
        return accounts.any { it.username == username }
    }

    override suspend fun authenticateAccount(account: Account, credentials: Credentials): Boolean {
        check(account is SecureAccount)
        check(credentials is PasswordCredentials)

        val authenticated = if (account.password.isEmpty())
            credentials.password.isEmpty()
        else
            core.hash.compare(credentials.password.toCharArray(), account.password)

        if (authenticated) {
            val now = Instant.now()
            core.eventBus.publish(AccountLoginEvent(account, now))
            account.lastLogin = now
            logger.debug("Account ${account.id} logged-in.")
            updateAccount(account)
        }

        return authenticated
    }

}