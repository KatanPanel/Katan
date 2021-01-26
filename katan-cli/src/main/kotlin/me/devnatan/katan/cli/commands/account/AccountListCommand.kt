package me.devnatan.katan.cli.commands.account

import com.github.ajalt.clikt.core.CliktCommand
import com.jakewharton.picnic.TextAlignment
import com.jakewharton.picnic.table
import me.devnatan.katan.cli.KatanCLI
import me.devnatan.katan.cli.render
import me.devnatan.katan.common.KatanTranslationKeys.CLI_ACCOUNT_LIST
import me.devnatan.katan.common.KatanTranslationKeys.CLI_ACCOUNT_LIST_ID
import me.devnatan.katan.common.KatanTranslationKeys.CLI_ACCOUNT_LIST_REGISTERED_AT
import me.devnatan.katan.common.KatanTranslationKeys.CLI_ACCOUNT_LIST_USERNAME
import me.devnatan.katan.common.KatanTranslationKeys.CLI_ALIAS_ACCOUNT_LIST
import me.devnatan.katan.common.KatanTranslationKeys.CLI_HELP_ACCOUNT_LIST
import me.devnatan.katan.common.util.dateTimeFormatter

class AccountListCommand(private val cli: KatanCLI) : CliktCommand(
    name = cli.translate(CLI_ALIAS_ACCOUNT_LIST),
    help = cli.translate(CLI_HELP_ACCOUNT_LIST)
) {

    override fun run() {
        val accounts = cli.accountManager.getAccounts()
        render(table {
            cellStyle {
                paddingLeft = 1
                paddingRight = 1
                borderLeft = true
                borderRight = true
            }

            header {
                cellStyle { alignment = TextAlignment.BottomCenter }
                row {
                    cell(cli.translate(CLI_ACCOUNT_LIST, accounts.size)) {
                        columnSpan = 4
                        paddingBottom = 1
                    }
                }

                row("#",
                    cli.translate(CLI_ACCOUNT_LIST_ID),
                    cli.translate(CLI_ACCOUNT_LIST_USERNAME),
                    cli.translate(CLI_ACCOUNT_LIST_REGISTERED_AT)
                )
            }

            body {
                for ((index, account) in accounts.sortedByDescending { it.registeredAt }.withIndex()) {
                    row(index + 1, account.id.toString().substringBefore("-"),
                        account.username,
                        dateTimeFormatter.format(account.registeredAt),
                    )
                }
            }
        })
    }

}