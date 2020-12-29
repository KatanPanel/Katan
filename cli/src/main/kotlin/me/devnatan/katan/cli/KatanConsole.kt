package me.devnatan.katan.cli

import com.github.ajalt.clikt.output.CliktConsole
import org.slf4j.Logger

class KatanConsole(private val logger: Logger) : CliktConsole {

    // SLF4J logger already adds the line break
    override val lineSeparator: String = ""

    override fun print(text: String, error: Boolean) {
        if (error) logger.error(text)
        else logger.info(text)
    }

    override fun promptForLine(prompt: String, hideInput: Boolean) = when {
        hideInput -> console.readPassword(prompt)?.let { String(it) }
        else -> console.readLine(prompt)
    }

    companion object {
        val console: java.io.Console by lazy { System.console() }
    }
}