package me.devnatan.katan.bootstrap

import com.fasterxml.jackson.annotation.JsonInclude
import com.fasterxml.jackson.core.util.DefaultIndenter
import com.fasterxml.jackson.core.util.DefaultPrettyPrinter
import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.databind.SerializationFeature
import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import io.ktor.application.*
import io.ktor.features.*
import io.ktor.http.*
import io.ktor.jackson.*
import io.ktor.response.*
import io.ktor.util.*
import io.ktor.websocket.*
import kotlinx.coroutines.runBlocking
import me.devnatan.katan.api.io.http.HttpResponse
import me.devnatan.katan.core.Katan
import me.devnatan.katan.core.KatanConfiguration
import org.slf4j.LoggerFactory
import java.io.File
import java.nio.file.Files
import kotlin.system.exitProcess
import kotlin.system.measureTimeMillis

private class KatanLauncher(
    private val application: Application,
    private val config: Map<*, *>,
    private val objectMapper: ObjectMapper
) {

    private companion object {
        val logger = LoggerFactory.getLogger(KatanLauncher::class.java)!!
    }

    init {
        logger.info("Initializing...")

        val time = measureTimeMillis {
            runBlocking {
                try {
                    Katan(KatanConfiguration(config), objectMapper).start()
                } catch (e: RuntimeException) {
                    exitProcess(0)
                }
            }

            installHooks()
        }

        logger.info("Katan initialized, took {}ms.", time)
    }

    private fun installHooks() = application.apply {
        install(DefaultHeaders)
        install(Compression)
        install(CallLogging)
        install(AutoHeadResponse)

        install(ContentNegotiation) {
            register(ContentType.Application.Json, JacksonConverter(objectMapper))
        }

        install(CORS) {
            anyHost()
            allowCredentials = true
            listOf(HttpMethod("PATCH"), HttpMethod.Put, HttpMethod.Delete).forEach {
                method(it)
            }
        }

        install(StatusPages) {
            exception<Throwable> { cause ->
                environment.log.error(cause)
                call.respond(HttpStatusCode.InternalServerError, HttpResponse.Error(500, cause.toString()))
            }
        }

        install(WebSockets)
    }

}

fun Application.main() {
    val config = File("katan.json")
    if (!config.exists())
        Files.copy(Katan::class.java.classLoader.getResourceAsStream(config.name)
            ?: throw IllegalArgumentException(config.name), config.toPath())

    val objectMapper = jacksonObjectMapper().apply {
        enable(SerializationFeature.INDENT_OUTPUT)
        setSerializationInclusion(JsonInclude.Include.NON_NULL)
        setDefaultPrettyPrinter(DefaultPrettyPrinter().apply {
            indentArraysWith(DefaultPrettyPrinter.FixedSpaceIndenter.instance)
            indentObjectsWith(DefaultIndenter("  ", "\n"))
        })
    }

    KatanLauncher(this, objectMapper.readValue(config, Map::class.java), objectMapper)
}