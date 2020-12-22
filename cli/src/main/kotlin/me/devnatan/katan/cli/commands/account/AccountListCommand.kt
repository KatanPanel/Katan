package me.devnatan.katan.cli.commands.account

import com.github.ajalt.clikt.core.CliktCommand
import com.jakewharton.picnic.TextAlignment
import com.jakewharton.picnic.renderText
import com.jakewharton.picnic.table
import me.devnatan.katan.cli.KatanCLI
import me.devnatan.katan.common.util.dateTimeFormatter

class AccountListCommand(private val cli: KatanCLI) : CliktCommand(
    name = "ls",
    help = "Lists all registered accounts."
) {

    override fun run() {
        val accounts = cli.accountManager.getAccounts()

        table {
            cellStyle {
                paddingLeft = 1
                paddingRight = 1
                borderLeft = true
                borderRight = true
            }

            header {
                cellStyle { alignment = TextAlignment.BottomCenter }
                row {
                    cell(cli.katan.translator.translate("cli.accounts-list", accounts.size)) {
                        columnSpan = 4
                        paddingBottom = 1
                    }
                }

                row("#",
                    cli.katan.translator.translate("cli.accounts-list-id"),
                    cli.katan.translator.translate("cli.accounts-list-username"),
                    cli.katan.translator.translate("cli.accounts-list-registered-at")
                )
            }

            body {
                for ((index, account) in accounts.sortedByDescending { account -> account.registeredAt }.withIndex()) {
                    row(index + 1, account.id.toString().substringBefore("-"),
                        account.username,
                        dateTimeFormatter.format(account.registeredAt),
                    )
                }
            }
        }.renderText().split(System.lineSeparator()).forEach(::echo)
    }

}