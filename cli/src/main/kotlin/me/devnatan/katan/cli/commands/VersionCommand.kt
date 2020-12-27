package me.devnatan.katan.cli.commands

import com.github.ajalt.clikt.core.CliktCommand
import me.devnatan.katan.api.Katan
import me.devnatan.katan.cli.KatanCLI
import me.devnatan.katan.common.KatanTranslationKeys.CLI_ALIAS_VERSION
import me.devnatan.katan.common.KatanTranslationKeys.CLI_HELP_VERSION
import me.devnatan.katan.common.KatanTranslationKeys.CLI_VERSION_ENVIRONMENT
import me.devnatan.katan.common.KatanTranslationKeys.CLI_VERSION_PLATFORM
import me.devnatan.katan.common.KatanTranslationKeys.CLI_VERSION_RUNNING_ON
import me.devnatan.katan.common.KatanTranslationKeys.KATAN_ENV

class VersionCommand(private val cli: KatanCLI) : CliktCommand(
    name = cli.translate(CLI_ALIAS_VERSION),
    help = cli.translate(CLI_HELP_VERSION)
) {

    override fun run() {
        echo(cli.translate(CLI_VERSION_RUNNING_ON, Katan.VERSION))
        echo(cli.translate(CLI_VERSION_PLATFORM, cli.katan.platform.toString()))
        echo(cli.translate(CLI_VERSION_ENVIRONMENT, cli.translate("${KATAN_ENV}.${cli.katan.environment}")))
    }

}