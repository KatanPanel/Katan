package me.devnatan.katan.cli.commands.account

import com.github.ajalt.clikt.core.CliktCommand
import com.github.ajalt.clikt.parameters.options.default
import com.github.ajalt.clikt.parameters.options.option
import com.github.ajalt.clikt.parameters.options.required
import kotlinx.coroutines.CoroutineName
import kotlinx.coroutines.launch
import me.devnatan.katan.cli.KatanCLI

class AccountRegisterCommand(private val cli: KatanCLI) : CliktCommand(
    name = "register",
    help = "Creates a new account."
) {

    private val username by option("-u", "--username").required()
    private val password by option("-p", "--password").default("")

    override fun run() {
        if (cli.accountManager.existsAccount(username))
            return echo(cli.katan.translator.translate("cli.account-already-registered", username))

        cli.coroutineScope.launch(CoroutineName("KatanCLI::account-create:$username")) {
            cli.accountManager.registerAccount(cli.accountManager.createAccount(username, password))
        }.invokeOnCompletion {
            echo(cli.katan.translator.translate("cli.account-created", username))
        }
    }

}