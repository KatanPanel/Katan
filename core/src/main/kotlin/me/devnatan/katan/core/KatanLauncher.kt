package me.devnatan.katan.core

import com.typesafe.config.Config
import com.typesafe.config.ConfigFactory
import kotlinx.coroutines.runBlocking
import me.devnatan.katan.api.*
import me.devnatan.katan.cli.KatanCLI
import me.devnatan.katan.common.util.exportResource
import me.devnatan.katan.common.util.get
import me.devnatan.katan.common.util.loadResource
import me.devnatan.katan.core.KatanCore.Companion.DEFAULT_VALUE
import me.devnatan.katan.webserver.KatanWS
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import java.io.BufferedReader
import java.io.File
import java.io.InputStreamReader
import java.util.*
import kotlin.system.exitProcess
import kotlin.system.measureTimeMillis

private object KatanLauncher {

    private const val TRANSLATION_FILE_PATTERN = "translations/%s.properties"
    private const val FALLBACK_LANGUAGE = "en"

    private val logger: Logger by lazy {
        LoggerFactory.getLogger(KatanLauncher::class.java)
    }

    private fun checkEnvironment(): KatanEnvironment {
        val env = System.getProperty(Katan.ENVIRONMENT_PROPERTY, KatanEnvironment.PRODUCTION).toLowerCase()
        if (env !in KatanEnvironment.ALL) {
            System.err.println(
                "Environment \"$env\" is not valid for Katan. You can only choose these: ${
                    KatanEnvironment.ALL.joinToString(", ")
                }"
            )
            exitProcess(0)
        }

        return KatanEnvironment(env).also {
            System.setProperty("katan.log.level", it.defaultLogLevel().toString())
        }
    }

    private fun loadConfig(environment: KatanEnvironment): Config {
        var config = ConfigFactory.parseFile(exportResource("katan.conf"))

        val environmentConfig = File("katan.$environment.conf")
        if (environmentConfig.exists()) {
            config = ConfigFactory.parseFile(environmentConfig).withFallback(config)
        } else {
            val localConfig = File("katan.local.conf")
            if (localConfig.exists())
                config = ConfigFactory.parseFile(localConfig).withFallback(config)
        }

        return config
    }

    private fun loadTranslations(config: Config): Translator {
        var userLocale: Locale = if (config.get("locale", DEFAULT_VALUE) == DEFAULT_VALUE)
            Locale.getDefault()
        else
            Locale.forLanguageTag(config.get("locale", FALLBACK_LANGUAGE))

        val translations = runCatching {
            loadResource(TRANSLATION_FILE_PATTERN.format(userLocale.toLanguageTag()))
        }.getOrElse {
            runCatching {
                loadResource(TRANSLATION_FILE_PATTERN.format(userLocale.toLanguageTag().substringBefore("-")))
            }.getOrNull()
        } ?: run {
            logger.error("Language \"${userLocale.toLanguageTag()}\" is not supported by Katan.")
            logger.error("We will use the fallback language for messages, change the language in the configuration file to one that is supported.")

            userLocale = Locale(FALLBACK_LANGUAGE)
            loadResource(TRANSLATION_FILE_PATTERN.format(userLocale.toLanguageTag()))
        }

        System.setProperty(Katan.LOCALE_PROPERTY, userLocale.toLanguageTag())
        return MapBasedTranslator(userLocale, Properties().apply {
            // force UTF-8 encoding
            BufferedReader(
                InputStreamReader(
                    translations,
                    Charsets.UTF_8
                )
            ).use { input -> load(input) }
        }.mapKeys { (key, _) -> key.toString() })
    }

    @JvmStatic
    fun main(args: Array<out String>) {
        val environment = checkEnvironment()
        val config = loadConfig(environment)
        val translator = loadTranslations(config)
        val core = KatanCore(config, environment, translator)
        val cli = KatanCLI(core)
        val webServer = KatanWS(core)

        Runtime.getRuntime().addShutdownHook(Thread {
            runBlocking {
                cli.close()
                webServer.close()
                core.close()
            }
        })

        runBlocking {
            val time = measureTimeMillis {
                core.start()
            }

            if (webServer.enabled)
                webServer.init()

            logger.info(core.translator.translate("katan.started", String.format("%.2f", time / 1000.0f)))
            cli.init()
        }
    }

}
