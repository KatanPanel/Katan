package me.devnatan.katan.cli.commands.account

import com.github.ajalt.clikt.core.CliktCommand
import com.github.ajalt.clikt.parameters.options.flag
import com.github.ajalt.clikt.parameters.options.option
import me.devnatan.katan.cli.KatanCLI
import me.devnatan.katan.common.account.SecureAccount

class AccountListCommand(private val cli: KatanCLI) : CliktCommand(
    name = "ls",
    help = "Lists all registered accounts."
) {

    private val detailed by option("-d", "--detailed").flag()

    override fun run() {
        val accounts = cli.accountManager.getAccounts()
        echo("List of all accounts (${accounts.size}):")
        for (account in accounts) {
            echo(buildString {
                if (detailed)
                    append("${account.id} - ")

                append(account.username)
                if (account is SecureAccount && account.password.isNotEmpty())
                    append(" (with password)")
            })
        }
    }

}