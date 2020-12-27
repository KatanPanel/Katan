package me.devnatan.katan.cli.commands.account

import com.github.ajalt.clikt.core.NoOpCliktCommand
import com.github.ajalt.clikt.core.subcommands
import me.devnatan.katan.cli.KatanCLI
import me.devnatan.katan.common.KatanTranslationKeys.CLI_ALIAS_ACCOUNT
import me.devnatan.katan.common.KatanTranslationKeys.CLI_ALIAS_ACCOUNT_LIST
import me.devnatan.katan.common.KatanTranslationKeys.CLI_ALIAS_ACCOUNT_REGISTER
import me.devnatan.katan.common.KatanTranslationKeys.CLI_HELP_ACCOUNT

class AccountCommand(private val cli: KatanCLI) : NoOpCliktCommand(
    name = cli.translate(CLI_ALIAS_ACCOUNT),
    help = cli.translate(CLI_HELP_ACCOUNT),
    printHelpOnEmptyArgs = true
) {

    init {
        subcommands(AccountListCommand(cli), AccountRegisterCommand(cli))
    }

    override fun aliases(): Map<String, List<String>> {
        return mapOf(
            "register" to listOf(cli.translate(CLI_ALIAS_ACCOUNT_REGISTER)),
            "ls" to listOf(cli.translate(CLI_ALIAS_ACCOUNT_LIST))
        )
    }

}