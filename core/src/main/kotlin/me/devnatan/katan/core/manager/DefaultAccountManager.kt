package me.devnatan.katan.core.manager

import kotlinx.coroutines.runBlocking
import me.devnatan.katan.api.account.Account
import me.devnatan.katan.api.manager.AccountManager
import me.devnatan.katan.common.account.SecureAccount
import me.devnatan.katan.core.KatanCore
import me.devnatan.katan.core.repository.AccountsRepository
import org.slf4j.LoggerFactory
import java.time.Instant
import java.util.*

class DefaultAccountManager(
    private val core: KatanCore,
    private val repository: AccountsRepository
) : AccountManager {

    private companion object {

        val logger = LoggerFactory.getLogger(AccountManager::class.java)!!

    }

    private val accounts = hashSetOf<Account>()

    init {
        runBlocking {
            accounts.addAll(repository.listAccounts())
        }
    }

    override fun getAccounts(): List<Account> {
        return accounts.toList()
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
            this.password = core.hash.hash(password)
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
        return core.hash.compare(password, account.password!!)
    }

}