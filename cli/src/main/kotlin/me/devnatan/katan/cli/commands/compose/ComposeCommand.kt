package me.devnatan.katan.cli.commands.compose

import com.github.ajalt.clikt.core.NoOpCliktCommand
import com.github.ajalt.clikt.core.subcommands
import me.devnatan.katan.cli.KatanCLI

class ComposeCommand(cli: KatanCLI) : NoOpCliktCommand(
    name = "compose",
    help = cli.locale["cli.help.compose"],
    printHelpOnEmptyArgs = true
) {

    init {
        subcommands()
    }

}