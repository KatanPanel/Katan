package me.devnatan.katan.cli.commands

import com.github.ajalt.clikt.core.CliktCommand
import com.github.ajalt.clikt.core.NoOpCliktCommand
import com.github.ajalt.clikt.core.context
import com.github.ajalt.clikt.core.subcommands
import com.github.ajalt.clikt.parameters.arguments.argument
import com.github.ajalt.clikt.parameters.options.default
import com.github.ajalt.clikt.parameters.options.option
import kotlinx.coroutines.CoroutineName
import kotlinx.coroutines.launch
import me.devnatan.katan.cli.KatanCLI

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

    override fun run() {
        val accounts = cli.accountManager.getAccounts()
        echo("List of all accounts (${accounts.size}):")
        for (account in accounts) {
            echo("${account.id} - ${account.username}")
        }
    }

}

class AccountsCreateCommand(private val cli: KatanCLI) : CliktCommand(
    name = "create",
    help = "Creates a new account."
) {

    private val accountName by argument("name", "")
    private val accountPassword by option("--password", "-p").default("")

    override fun run() {
        if (cli.accountManager.existsAccount(accountName))
            return echo("There is already an account registered as $accountName.")

        echo("Creating account $accountName...")
        val account = cli.accountManager.createAccount(accountName, accountPassword)
        cli.coroutineScope.launch(cli.executor + CoroutineName("KatanCLI::account-create:$accountName")) {
            cli.accountManager.registerAccount(account)
        }.invokeOnCompletion {
            echo("Account $accountName created successfully.")
        }
    }

}