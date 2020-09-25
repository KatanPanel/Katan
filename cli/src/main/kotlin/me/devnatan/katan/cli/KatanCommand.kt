package me.devnatan.katan.cli

import com.github.ajalt.clikt.core.CliktCommand
import com.github.ajalt.clikt.core.context
import com.github.ajalt.clikt.parameters.options.flag
import com.github.ajalt.clikt.parameters.options.option
import me.devnatan.katan.common.KATAN_VERSION

class KatanCommand : CliktCommand(
    name = "katan",
    printHelpOnEmptyArgs = true,
    invokeWithoutSubcommand = true
) {

    private val version by option("-v", help = "Shows Katan current version.").flag()

    init {
        context {
            console = KatanCLI.Console
        }
    }

    override fun run() {
        if (version)
            KatanCLI.showVersion()
    }

}