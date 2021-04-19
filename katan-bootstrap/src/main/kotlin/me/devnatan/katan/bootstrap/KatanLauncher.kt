package me.devnatan.katan.bootstrap

import com.typesafe.config.Config
import com.typesafe.config.ConfigFactory
import kotlinx.coroutines.*
import me.devnatan.katan.api.*
import me.devnatan.katan.api.logging.logger
import me.devnatan.katan.cli.KatanCLI
import me.devnatan.katan.common.EnvKeys
import me.devnatan.katan.common.util.exportResource
import me.devnatan.katan.common.util.get
import me.devnatan.katan.common.util.getEnv
import me.devnatan.katan.common.util.loadResource
import me.devnatan.katan.core.KatanCore
import me.devnatan.katan.core.KatanCore.Companion.DEFAULT_VALUE
import me.devnatan.katan.io.file.DefaultFileSystemAccessor
import me.devnatan.katan.io.file.DockerHostFileSystem
import me.devnatan.katan.io.file.PersistentFileSystem
import me.devnatan.katan.webserver.KatanWS
import java.io.BufferedReader
import java.io.File
import java.io.InputStreamReader
import java.util.*
import kotlin.system.exitProcess
import kotlin.system.measureTimeMillis

class KatanLauncher {

    companion object {

        private val log by lazy { logger<KatanLauncher>() }

        private const val TRANSLATION_FILE_PATTERN =
            "translations/%s.properties"
        private const val FALLBACK_LANGUAGE = "en"

        @OptIn(ExperimentalCoroutinesApi::class)
        @JvmStatic
        fun main(args: Array<out String>) {
            val boot = KatanLauncher()
            val environment = boot.checkEnvironment()

            val root = File(System.getenv(EnvKeys.ROOT_DIR) ?: System.getProperty("user.dir"))
            val config = boot.loadConfig(root, environment)

            val core = KatanCore(
                config,
                environment,
                boot.loadTranslations(config),
                root
            )
            val ws = KatanWS.Initializer.create(core)
            val cli = KatanCLI(core)
            Runtime.getRuntime().addShutdownHook(Thread {
                runBlocking(CoroutineName("Katan Shutdown")) {
                    ws?.close()
                    cli.close()
                    core.close()
                }
            })

            val time = runBlocking {
                measureTimeMillis {
                    core.start()
                    ws?.start()
                }
            }

            log.info(
                core.translator.translate(
                    "katan.started",
                    String.format("%.2f", time / 1000.0f)
                )
            )
            cli.init()
        }
    }

    private fun checkEnvironment(): KatanEnvironment {
        val env = System.getProperty(
            Katan.ENVIRONMENT_PROPERTY,
            KatanEnvironment.PRODUCTION
        ).toLowerCase()
        if (env !in KatanEnvironment.ALL) {
            System.err.println(
                "Environment mode \"$env\" is not valid. You can only choose these: ${
                    KatanEnvironment.ALL.joinToString(", ")
                }."
            )
            exitProcess(0)
        }

        return KatanEnvironment(env).also {
            System.setProperty(
                Katan.LOG_LEVEL_PROPERTY,
                it.defaultLogLevel().toString()
            )
            System.setProperty(
                Katan.LOG_PATTERN_PROPERTY, if (it.isProduction())
                    "[%d{yyyy-MM-dd HH:mm:ss}] [%-4level]: %msg%n"
                else
                    "[%d{yyyy-MM-dd HH:mm:ss}] [%t/%-4level @ %logger{1}]: %msg%n"
            )

            System.setProperty(
                DEBUG_PROPERTY_NAME, if (it.isDevelopment())
                    DEBUG_PROPERTY_VALUE_ON
                else
                    DEBUG_PROPERTY_VALUE_AUTO
            )
        }
    }

    private fun loadConfig(root: File, environment: KatanEnvironment): Config {
        var config = ConfigFactory.parseFile(exportResource("katan.conf", root))

        val environmentConfig = File(root, "katan.$environment.conf")
        if (environmentConfig.exists()) {
            config =
                ConfigFactory.parseFile(environmentConfig).withFallback(config)
        } else {
            val localConfig = File(root, "katan.local.conf")
            if (localConfig.exists())
                config =
                    ConfigFactory.parseFile(localConfig).withFallback(config)
        }

        return config
    }

    private fun loadTranslations(config: Config): Translator {
        val definedLocale = config.getEnv("locale", EnvKeys.LOCALE)
        var userLocale = if (definedLocale == null || definedLocale == "default")
            Locale.getDefault()
        else
            Locale.forLanguageTag(definedLocale)

        val translations = runCatching {
            loadResource(TRANSLATION_FILE_PATTERN.format(userLocale.toLanguageTag()))
        }.getOrElse {
            runCatching {
                loadResource(
                    TRANSLATION_FILE_PATTERN.format(
                        userLocale.toLanguageTag().substringBefore("-")
                    )
                )
            }.getOrNull()
        } ?: run {
            log.error("Language \"${userLocale.toLanguageTag()}\" is not supported.")
            log.error(
                "We will use the fallback language ($FALLBACK_LANGUAGE) for " +
                        "messages, " +
                        "change the language in the configuration file to one that is supported."
            )

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

}
