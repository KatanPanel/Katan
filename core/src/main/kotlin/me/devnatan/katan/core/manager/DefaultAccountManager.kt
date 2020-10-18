package me.devnatan.katan.core.manager

import me.devnatan.katan.api.security.account.Account
import me.devnatan.katan.api.security.account.AccountManager
import me.devnatan.katan.common.account.SecureAccount
import me.devnatan.katan.core.KatanCore
import me.devnatan.katan.core.repository.AccountsRepository
import java.time.Instant
import java.util.*

class DefaultAccountManager(
    private val core: KatanCore,
    private val repository: AccountsRepository
) : AccountManager {

    private val accounts = hashSetOf<Account>()

    internal suspend fun loadAccounts() {
        accounts.addAll(repository.listAccounts())
    }

    override fun getAccounts(): List<Account> {
        return synchronized(accounts) {
            accounts.toList()
        }
    }

    override suspend fun getAccount(username: String): Account? {
        return synchronized(accounts) {
            accounts.find { it.username == username }
        }
    }

    override suspend fun getAccount(id: UUID): Account? {
        return synchronized(accounts) {
            accounts.find { it.id == id }
        }
    }

    override fun createAccount(username: String, password: String): Account {
        val account = SecureAccount(UUID.randomUUID(), username, Instant.now()).apply {
            this.password = if (password.isNotBlank())
                core.hash.hash(password.toCharArray())
            else password
        }

        return synchronized(accounts) {
            if (!accounts.add(account))
                throw IllegalArgumentException(username)

            account
        }
    }

    override suspend fun registerAccount(account: Account) {
        repository.insertAccount(account)
    }

    override fun existsAccount(username: String): Boolean {
        return accounts.any { it.username == username }
    }

    override suspend fun authenticateAccount(account: Account, password: String): Boolean {
        check(account is SecureAccount)
        return if (account.password.isEmpty())
            password.isEmpty()
        else
            core.hash.compare(password.toCharArray(), account.password)
    }

}