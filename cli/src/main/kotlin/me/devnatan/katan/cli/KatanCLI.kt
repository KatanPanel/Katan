package me.devnatan.katan.cli

import com.github.ajalt.clikt.core.PrintHelpMessage
import com.github.ajalt.clikt.core.UsageError
import com.github.ajalt.clikt.output.CliktConsole
import kotlinx.coroutines.*
import me.devnatan.katan.api.Katan
import me.devnatan.katan.common.KATAN_VERSION
import org.slf4j.LoggerFactory
import java.io.Closeable
import java.util.concurrent.Executors

class KatanCLI(katan: Katan) : Closeable, Katan by katan {

    companion object {
        val logger = LoggerFactory.getLogger(KatanCLI::class.java)!!

        internal fun showVersion() {
            logger.info("Running on Katan v$KATAN_VERSION.")
        }
    }

    object Console : CliktConsole {

        private val console = System.console()
        override val lineSeparator: String = System.lineSeparator()

        override fun print(text: String, error: Boolean) {
            if (error) logger.error(text)
            else logger.info(text)
        }

        override fun promptForLine(prompt: String, hideInput: Boolean) = when {
            hideInput -> console.readPassword(prompt)?.let { String(it) }
            else -> console.readLine(prompt)
        }
    }

    private var running = false
    var executor = Executors.newSingleThreadExecutor().asCoroutineDispatcher()
    var coroutineScope = CoroutineScope(CoroutineName("KatanCLI"))
    private val command = KatanCommand(this)

    suspend fun init() {
        coroutineScope.launch {
            var line: String?
            do {
                line = readLine()
                try {
                    val args = line?.split(" ") ?: emptyList()
                    if (!args[0].equals("katan", true))
                        continue

                    if (args.size == 1) {
                        showVersion()
                        continue
                    }

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

        running = true
    }

    override fun close() {
        executor.close()
        coroutineScope.cancel()
    }

}