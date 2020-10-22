package me.devnatan.katan.cli.commands.account

import com.github.ajalt.clikt.core.CliktCommand
import me.devnatan.katan.cli.KatanCLI
import me.devnatan.katan.common.account.SecureAccount

class AccountListCommand(private val cli: KatanCLI) : CliktCommand(
    name = "ls",
    help = "Lists all registered accounts."
) {

    override fun run() {
        val accounts = cli.accountManager.getAccounts()
        echo(cli.locale["cli.accounts-list", accounts.size])
        for (account in accounts.map { it as SecureAccount }) {
            val accountId = account.id.toString().substringBefore("-")
            val registeredAt = cli.katan.dateTimeFormatter.format(account.registeredAt)
            if (account.password.isNotEmpty())
                echo(cli.locale["cli.accounts-list-secure", accountId, account.username, registeredAt])
            else
                echo(cli.locale["cli.accounts-list-insecure", accountId, account.username, registeredAt])
        }
    }

}