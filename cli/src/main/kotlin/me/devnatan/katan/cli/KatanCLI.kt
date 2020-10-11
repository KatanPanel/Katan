package me.devnatan.katan.cli

import com.github.ajalt.clikt.core.PrintHelpMessage
import com.github.ajalt.clikt.core.UsageError
import com.github.ajalt.clikt.output.CliktConsole
import kotlinx.coroutines.*
import me.devnatan.katan.api.manager.AccountManager
import me.devnatan.katan.api.manager.PluginManager
import me.devnatan.katan.api.manager.ServerManager
import me.devnatan.katan.core.KatanCore
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import java.util.concurrent.Executors

class KatanCLI(val katan: KatanCore) {

    class Console(private val logger: Logger) : CliktConsole {

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

    val logger = LoggerFactory.getLogger(KatanCLI::class.java)!!

    val serverManager: ServerManager get() = katan.serverManager
    val accountManager: AccountManager get() = katan.accountManager
    val pluginManager: PluginManager get() = katan.pluginManager

    private var running = false
    private val command = KatanCommand(this)
    val coroutineExecutor = Executors.newSingleThreadExecutor().asCoroutineDispatcher()
    val coroutineScope = CoroutineScope(CoroutineName("KatanCLI"))
    val console = Console(logger)

    fun init() {
        running = true
        var line: String?
        do {
            line = readLine()
            try {
                val args = line?.split(" ") ?: emptyList()
                if (!args[0].equals("katan", true))
                    continue

                if (args.size == 1)
                    throw PrintHelpMessage(command)

                command.parse(args.subList(1, args.size))
            } catch (e: PrintHelpMessage) {
                logger.info(e.command.getFormattedHelp())
            } catch (e: UsageError) {
                logger.error(e.message)
            } catch (e: Throwable) {
                e.printStackTrace()
            }
        } while (line != null)
    }

    fun close() {
        if (coroutineExecutor.isActive)
            coroutineScope.cancel()
        coroutineExecutor.close()
    }

}