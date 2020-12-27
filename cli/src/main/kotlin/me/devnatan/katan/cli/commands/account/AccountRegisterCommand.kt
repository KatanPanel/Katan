package me.devnatan.katan.cli.commands.account

import com.github.ajalt.clikt.core.CliktCommand
import com.github.ajalt.clikt.parameters.options.default
import com.github.ajalt.clikt.parameters.options.option
import com.github.ajalt.clikt.parameters.options.required
import kotlinx.coroutines.launch
import me.devnatan.katan.cli.KatanCLI
import me.devnatan.katan.cli.err
import me.devnatan.katan.common.KatanTranslationKeys.CLI_ACCOUNT_ALREADY_REGISTERED
import me.devnatan.katan.common.KatanTranslationKeys.CLI_ACCOUNT_CREATED
import me.devnatan.katan.common.KatanTranslationKeys.CLI_ALIAS_ACCOUNT_REGISTER
import me.devnatan.katan.common.KatanTranslationKeys.CLI_HELP_ACCOUNT_REGISTER
import me.devnatan.katan.common.KatanTranslationKeys.CLI_OPTION_ACCOUNT_PASSWORD
import me.devnatan.katan.common.KatanTranslationKeys.CLI_OPTION_ACCOUNT_USERNAME

class AccountRegisterCommand(private val cli: KatanCLI) : CliktCommand(
    name = cli.translate(CLI_ALIAS_ACCOUNT_REGISTER),
    help = cli.translate(CLI_HELP_ACCOUNT_REGISTER)
) {

    private val username by option("-u", help = cli.translate(CLI_OPTION_ACCOUNT_USERNAME)).required()
    private val password by option("-p", help = cli.translate(CLI_OPTION_ACCOUNT_PASSWORD)).default("")

    override fun run() {
        if (cli.accountManager.existsAccount(username))
            return err(cli.translate(CLI_ACCOUNT_ALREADY_REGISTERED, username))

        cli.coroutineScope.launch {
            cli.accountManager.registerAccount(cli.accountManager.createAccount(username, password))
        }.invokeOnCompletion {
            echo(cli.translate(CLI_ACCOUNT_CREATED, username))
        }
    }

}