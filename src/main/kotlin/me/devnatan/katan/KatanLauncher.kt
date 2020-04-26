package me.devnatan.katan

import com.fasterxml.jackson.annotation.JsonInclude
import com.fasterxml.jackson.databind.SerializationFeature
import io.ktor.application.Application
import io.ktor.application.call
import io.ktor.application.install
import io.ktor.application.log
import io.ktor.features.*
import io.ktor.http.HttpMethod
import io.ktor.http.HttpStatusCode
import io.ktor.jackson.jackson
import io.ktor.response.respond
import io.ktor.routing.routing
import io.ktor.util.error
import io.ktor.websocket.WebSockets
import me.devnatan.katan.api.io.http.KHttpResponse
import kotlin.system.measureTimeMillis

fun Application.main() {
    KatanLauncher(this).launch()
}

class KatanLauncher(private val app: Application) {

    private lateinit var katan: Katan

    fun launch() {
        app.log.info("Initializing...")
        katan = Katan(app)
        val time = measureTimeMillis {
            app.installHooks()
            katan.router = KatanRouter(katan, app.routing {})
        }

        katan.boot()
        app.log.info("Katan initialized, took ${time}ms.")
    }

    private fun Application.installHooks() {
        log.info("Installing hooks...")
        install(DefaultHeaders)
        install(Compression)
        install(CallLogging)
        install(AutoHeadResponse)

        install(ContentNegotiation) {
            jackson {
                enable(SerializationFeature.INDENT_OUTPUT)
                setSerializationInclusion(JsonInclude.Include.NON_NULL)
                katan.jsonMapper = this
            }
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
                call.respond(HttpStatusCode.InternalServerError, KHttpResponse.Error(cause.toString()))
            }
        }

        install(WebSockets)
    }

}