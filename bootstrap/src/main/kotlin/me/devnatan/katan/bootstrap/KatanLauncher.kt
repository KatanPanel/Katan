package me.devnatan.katan.bootstrap

import com.typesafe.config.Config
import com.typesafe.config.ConfigFactory
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import me.devnatan.katan.api.Katan
import me.devnatan.katan.cli.KatanCLI
import me.devnatan.katan.common.util.exportResource
import me.devnatan.katan.common.util.get
import me.devnatan.katan.core.KatanCore
import me.devnatan.katan.core.exceptions.SilentException
import me.devnatan.katan.webserver.KatanWebServer
import kotlin.system.exitProcess
import kotlin.system.measureTimeMillis

private class KatanLauncher(config: Config) {

    companion object {

        @JvmStatic
        fun main(args: Array<out String>) {
            KatanLauncher(ConfigFactory.parseFile(this::class.exportResource("katan.conf")))
        }

    }

    private val katan = KatanCore(config)
    private var cli: KatanCLI
    private var webServer: KatanWebServer

    init {
        runBlocking {
            try {
                val time = measureTimeMillis {
                    katan.start()
                }
                KatanCore.logger.info("Katan initialized took {}s.", String.format("%.2f", time / 1000.0f))
                KatanCore.logger.info("Type \"katan\" for more details.")
            } catch (e: Throwable) {
                when (e) {
                    is SilentException -> e.logger.error(e.cause.toString())
                    else -> e.printStackTrace()
                }
                exitProcess(0)
            }
        }

        cli = KatanCLI(katan)
        webServer = KatanWebServer(katan.accountManager, katan.serverManager)

        if (webServer.enabled)
            webServer.init()

        Runtime.getRuntime().addShutdownHook(Thread {
            runBlocking {
                cli.close()
                if (webServer.enabled)
                    webServer.close()
            }
        })

        cli.init()
    }

}