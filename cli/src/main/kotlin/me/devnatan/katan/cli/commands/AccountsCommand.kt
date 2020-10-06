package me.devnatan.katan.cli.commands

import com.github.ajalt.clikt.core.CliktCommand
import com.github.ajalt.clikt.core.NoOpCliktCommand
import com.github.ajalt.clikt.core.subcommands
import com.github.ajalt.clikt.parameters.options.default
import com.github.ajalt.clikt.parameters.options.flag
import com.github.ajalt.clikt.parameters.options.option
import com.github.ajalt.clikt.parameters.options.required
import kotlinx.coroutines.CoroutineName
import kotlinx.coroutines.launch
import me.devnatan.katan.cli.KatanCLI
import me.devnatan.katan.common.account.SecureAccount

class AccountsCommand(cli: KatanCLI) : NoOpCliktCommand(
    name = "account",
    help = "Create, remove and manage accounts.",
    printHelpOnEmptyArgs = true
) {

    init {
        subcommands(AccountsListCommand(cli), AccountsCreateCommand(cli))
    }

}

class AccountsListCommand(private val cli: KatanCLI) : CliktCommand(
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

class AccountsCreateCommand(private val cli: KatanCLI) : CliktCommand(
    name = "create",
    help = "Creates a new account."
) {

    private val username by option("-u", "--username").required()
    private val password by option("-p", "--password").default("")

    override fun run() {
        if (cli.accountManager.existsAccount(username))
            return echo("There is already an account registered as $username.")

        echo("Creating account $username...")
        cli.coroutineScope.launch(cli.coroutineExecutor + CoroutineName("KatanCLI::account-create:$username")) {
            cli.accountManager.registerAccount(cli.accountManager.createAccount(username, password))
        }.invokeOnCompletion {
            echo("Account $username created successfully.")
        }
    }

}