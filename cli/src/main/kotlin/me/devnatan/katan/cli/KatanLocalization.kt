package me.devnatan.katan.cli

import com.github.ajalt.clikt.output.Localization
import me.devnatan.katan.core.KatanLocale

class KatanLocalization(private val locale: KatanLocale) : Localization {

    override fun optionsTitle(): String {
        return locale["cli.extent.options-title"]
    }

    override fun usageTitle(): String {
        return locale["cli.extent.usage-title"]
    }

    override fun argumentsTitle(): String {
        return locale["cli.extent.args-title"]
    }

    override fun helpTagDefault(): String {
        return locale["cli.extent.tag-default"]
    }

    override fun helpTagRequired(): String {
        return locale["cli.extent.tag-required"]
    }

    override fun optionsMetavar(): String {
        return locale["cli.extent.meta-options"]
    }

    override fun commandMetavar(): String {
        return locale["cli.extent.meta-command"]
    }

    override fun helpOptionMessage(): String {
        return locale["cli.extent.help"]
    }

}