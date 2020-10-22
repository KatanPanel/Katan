package me.devnatan.katan.cli.commands.account

import com.github.ajalt.clikt.core.NoOpCliktCommand
import com.github.ajalt.clikt.core.subcommands
import me.devnatan.katan.cli.KatanCLI

class AccountCommand(cli: KatanCLI) : NoOpCliktCommand(
    name = "account",
    help = cli.locale["cli.help.accounts"],
    printHelpOnEmptyArgs = true
) {

    init {
        subcommands(AccountListCommand(cli), AccountRegisterCommand(cli))
    }

}