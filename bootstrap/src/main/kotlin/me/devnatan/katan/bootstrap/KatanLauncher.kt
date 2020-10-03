package me.devnatan.katan.bootstrap

import com.typesafe.config.Config
import com.typesafe.config.ConfigFactory
import kotlinx.coroutines.runBlocking
import me.devnatan.katan.api.KatanEnvironment
import me.devnatan.katan.api.defaultLogLevel
import me.devnatan.katan.cli.KatanCLI
import me.devnatan.katan.common.util.createDirectory
import me.devnatan.katan.common.util.exportResource
import me.devnatan.katan.common.util.get
import me.devnatan.katan.common.util.loadResource
import me.devnatan.katan.core.KatanCore
import me.devnatan.katan.core.KatanLocale
import me.devnatan.katan.core.exceptions.SilentException
import me.devnatan.katan.webserver.KatanWS
import java.io.BufferedReader
import java.io.FileInputStream
import java.io.InputStreamReader
import java.util.*
import kotlin.system.exitProcess
import kotlin.system.measureTimeMillis

private class KatanLauncher(config: Config, environment: KatanEnvironment, locale: KatanLocale) {

    companion object {

        @JvmStatic
        fun main(args: Array<out String>) {
            val config = ConfigFactory.parseFile(exportResource("katan.conf"))
            createDirectory("messages")
            val locale = config.get("locale", "en-US").let { desired ->
                val public = exportResource("messages/$desired.properties")
                val internal = loadResource("messages/$desired.internal.properties")

                KatanLocale(Locale.forLanguageTag(desired), Properties().apply {
                    BufferedReader(InputStreamReader(FileInputStream(public), Charsets.UTF_8)).use { load(it) }
                }, Properties().apply {
                    BufferedReader(InputStreamReader(internal, Charsets.UTF_8)).use { load(it) }
                })
            }

            val configEnv = config.get("environment", KatanEnvironment.DEVELOPMENT).toLowerCase()
            if (configEnv !in KatanEnvironment.ALL) {
                System.err.println(
                    locale.internal(
                        "invalid-environment",
                        "\"$configEnv\"",
                        KatanEnvironment.ALL.joinToString(", ")
                    )
                )
                return
            }

            val env = KatanEnvironment(configEnv)
            System.setProperty("katan.log.level", env.defaultLogLevel().toString())
            System.setProperty("katan.locale", locale.locale.toLanguageTag())

            KatanLauncher(config, env, locale)
        }

    }

    private val katan = KatanCore(config, environment, locale)
    private var cli: KatanCLI
    private var webServer: KatanWS

    init {
        runBlocking {
            try {
                val time = measureTimeMillis {
                    katan.start()
                }

                KatanCore.logger.info(katan.locale.internal("katan.started", String.format("%.2f", time / 1000.0f)))
            } catch (e: Throwable) {
                when (e) {
                    is SilentException -> e.logger.error(e.cause.toString())
                    else -> e.printStackTrace()
                }
                exitProcess(0)
            }

            cli = KatanCLI(katan)
            webServer = KatanWS(katan)

            if (webServer.enabled)
                webServer.init()

            Runtime.getRuntime().addShutdownHook(Thread {
                runBlocking {
                    cli.close()
                    if (webServer.enabled)
                        webServer.close()
                    katan.close()
                }
            })

            cli.init()
        }
    }

}