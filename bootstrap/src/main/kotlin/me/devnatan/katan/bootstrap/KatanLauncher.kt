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
import me.devnatan.katan.core.KatanCore
import me.devnatan.katan.core.KatanLocale
import me.devnatan.katan.core.exceptions.SilentException
import me.devnatan.katan.webserver.KatanWS
import java.io.BufferedReader
import java.io.File
import java.io.FileInputStream
import java.io.InputStreamReader
import java.util.*
import kotlin.system.exitProcess
import kotlin.system.measureTimeMillis

private class KatanLauncher(config: Config, environment: KatanEnvironment, locale: KatanLocale) {

    companion object {

        @JvmStatic
        fun main(args: Array<out String>) {
            val envVar = System.getProperty("katan.environment", "dev").toLowerCase()
            if (envVar !in KatanEnvironment.ALL)
                return System.err.println(
                    "Environment definition \"$envVar\" is not valid for Katan. You can only choose these: ${
                        KatanEnvironment.ALL.joinToString(
                            ", "
                        )
                    }"
                )

            var configEnv = File("katan.$envVar.conf")
            if (!configEnv.exists())
                configEnv = exportResource("katan.conf")
            else
                println("Configuration file: $configEnv")

            val config = ConfigFactory.parseFile(configEnv)
            createDirectory("translations")

            val userLocale: Locale = if (!config.hasPath("locale")) Locale.getDefault()
            else Locale.forLanguageTag(config.get("locale", "en-US"))

            val languageTag = userLocale.toLanguageTag()
            val messages = exportResource("translations/$languageTag.properties")

            val locale = KatanLocale(userLocale, Properties().apply {
                // force UTF-8 encoding
                BufferedReader(
                    InputStreamReader(
                        FileInputStream(messages),
                        Charsets.UTF_8
                    )
                ).use { input -> load(input) }
            })

            val env = System.getProperty("katan.environment", "dev").toLowerCase()
            if (env !in KatanEnvironment.ALL)
                return System.err.println(locale["invalid-environment", "\"$env\"", KatanEnvironment.ALL.joinToString(", ")])

            System.setProperty("katan.locale", locale.locale.toLanguageTag())
            KatanLauncher(config, KatanEnvironment(env).also {
                System.setProperty("katan.log.level", it.defaultLogLevel().toString())
            }, locale)
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

                KatanCore.logger.info(katan.locale["katan.started", String.format("%.2f", time / 1000.0f)])
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