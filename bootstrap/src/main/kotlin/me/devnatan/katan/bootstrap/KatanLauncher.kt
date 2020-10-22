package me.devnatan.katan.bootstrap

import com.typesafe.config.Config
import com.typesafe.config.ConfigFactory
import kotlinx.coroutines.*
import me.devnatan.katan.api.KatanEnvironment
import me.devnatan.katan.api.defaultLogLevel
import me.devnatan.katan.cli.KatanCLI
import me.devnatan.katan.common.exceptions.SilentException
import me.devnatan.katan.common.util.exportResource
import me.devnatan.katan.common.util.get
import me.devnatan.katan.core.KatanCore
import me.devnatan.katan.core.KatanCore.Companion.DEFAULT_VALUE
import me.devnatan.katan.core.KatanLocale
import me.devnatan.katan.webserver.KatanWS
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import java.io.BufferedReader
import java.io.File
import java.io.FileInputStream
import java.io.InputStreamReader
import java.util.*
import kotlin.system.exitProcess
import kotlin.system.measureTimeMillis

private class KatanLauncher(config: Config, environment: KatanEnvironment, locale: KatanLocale) {

    companion object {

        const val ENV_PROPERTY = "katan.environment"
        private val logger: Logger = LoggerFactory.getLogger(KatanLauncher::class.java)

        @JvmStatic
        fun main(args: Array<out String>) {
            val env = System.getProperty(ENV_PROPERTY, KatanEnvironment.DEVELOPMENT).toLowerCase()
            var configFile = File("katan.$env.conf")
            if (!configFile.exists())
                configFile = exportResource("katan.conf")

            if (env !in KatanEnvironment.ALL)
                return System.err.println(
                    "Environment \"$env\" is not valid for Katan. You can only choose these: ${
                        KatanEnvironment.ALL.joinToString(
                            ", "
                        )
                    }"
                )

            val config = ConfigFactory.parseFile(configFile)
            val userLocale: Locale = if (config.get("locale", DEFAULT_VALUE) == DEFAULT_VALUE) Locale.getDefault()
            else Locale.forLanguageTag(config.get("locale", "en-US"))

            val languageTag = userLocale.toLanguageTag()
            System.setProperty("katan.locale", languageTag)

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

            val katanEnv = KatanEnvironment(env)
            System.setProperty("katan.log.level", katanEnv.defaultLogLevel().toString())
            System.setProperty(
                DEBUG_PROPERTY_NAME, when {
                    katanEnv.isDevelopment() || katanEnv.isTesting() -> DEBUG_PROPERTY_VALUE_ON
                    katanEnv.isProduction() -> DEBUG_PROPERTY_VALUE_OFF
                    else -> DEBUG_PROPERTY_VALUE_AUTO
                }
            )
            KatanLauncher(config, katanEnv, locale)
        }

    }

    init {
        val katan = KatanCore(config, environment, locale)
        val cli = KatanCLI(katan)
        val webServer = KatanWS(katan)

        Runtime.getRuntime().addShutdownHook(Thread {
            runBlocking {
                cli.close()
                webServer.close()
                katan.close()
            }
        })

        runBlocking(CoroutineName("KatanLauncher")) {
            try {
                val time = measureTimeMillis {
                    katan.start()
                }

                logger.info(katan.locale["katan.started", String.format("%.2f", time / 1000.0f)])

                if (webServer.enabled)
                    webServer.init()

                cli.init()
            } catch (e: Throwable) {
                when (e) {
                    is SilentException -> {
                        logger.error("An error occurred while Katan starting @ ${e.logger.name.substringAfterLast(".")}:")
                        logger.error("\"${e.cause?.message ?: e.message}\"")
                        logger.trace(null, e)

                        if (e.exit)
                            exitProcess(0)
                    }
                    else -> e.printStackTrace()
                }
            }
        }
    }

}