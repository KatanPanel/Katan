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
import me.devnatan.katan.core.KatanLocale
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

        const val ENV_PROPERTY = "katan.environment"

        @JvmStatic
        fun main(args: Array<out String>) {
            val env = System.getProperty(ENV_PROPERTY, KatanEnvironment.DEVELOPMENT).toLowerCase()
            var configFile = File("katan.$env.conf")
            if (!configFile.exists())
                configFile = exportResource("katan.conf")

            if (env !in KatanEnvironment.ALL)
                return System.err.println(
                    "Environment definition \"$env\" is not valid for Katan. You can only choose these: ${
                        KatanEnvironment.ALL.joinToString(
                            ", "
                        )
                    }"
                )

            val config = ConfigFactory.parseFile(configFile)
            val userLocale: Locale = if (!config.hasPath("locale")) Locale.getDefault()
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

    private val katan = KatanCore(config, environment, locale)
    private var cli = KatanCLI(katan)
    private var webServer = KatanWS(katan)

    init {
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

                KatanCore.logger.info(katan.locale["katan.started", String.format("%.2f", time / 1000.0f)])

                if (webServer.enabled)
                    webServer.init()

                cli.init()
            } catch (e: Throwable) {
                when (e) {
                    is SilentException -> {
                        val logger = e.logger
                        val location = e.logger.name.substringAfterLast(".")
                        var message = "?"
                        var cause = "?"

                        e.cause?.let { error ->
                            if (error.cause == null) {
                                cause = error::class.qualifiedName!!
                                message = error.message!!
                            } else {
                                cause = error.cause!!::class.qualifiedName!!
                                message = error.cause!!.message!!
                            }
                        }

                        logger.error("+ ~~~~~~~ An error occurred while Katan starting ~~~~~~~ +")
                        logger.error("  Location: $location")
                        logger.error("  Cause: $cause")
                        logger.error("  Message: $message")
                        logger.error("+ ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~ +")

                        if (e.exit)
                            exitProcess(0)
                    }
                    else -> e.printStackTrace()
                }
            }
        }
    }

}