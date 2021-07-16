package me.devnatan.katan.cli

import com.github.ajalt.clikt.output.Localization

class KatanLocalization() : Localization {

    override fun optionsTitle(): String {
        return "" // translator.translate("cli.extent.options-title")
    }

    override fun usageTitle(): String {
        return "" // translator.translate("cli.extent.usage-title")
    }

    override fun argumentsTitle(): String {
        return "" // translator.translate("cli.extent.args-title")
    }

    override fun commandsTitle(): String {
        return "" // translator.translate("cli.extent.commands-title")
    }

    override fun helpTagDefault(): String {
        return "" // translator.translate("cli.extent.tag-default")
    }

    override fun helpTagRequired(): String {
        return "" // translator.translate("cli.extent.tag-required")
    }

    override fun optionsMetavar(): String {
        return "" // translator.translate("cli.extent.meta-options")
    }

    override fun commandMetavar(): String {
        return "" // translator.translate("cli.extent.meta-command")
    }

    override fun helpOptionMessage(): String {
        return "" // translator.translate("cli.extent.help")
    }

}