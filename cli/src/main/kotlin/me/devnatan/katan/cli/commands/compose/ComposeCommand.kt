package me.devnatan.katan.cli.commands.compose

import com.github.ajalt.clikt.core.NoOpCliktCommand
import com.github.ajalt.clikt.core.subcommands
import me.devnatan.katan.cli.KatanCLI

class ComposeCommand(cli: KatanCLI) : NoOpCliktCommand(
    name = "composer",
    help = cli.locale["cli.help.compose"],
    printHelpOnEmptyArgs = true
) {

    init {
        subcommands()
    }

}