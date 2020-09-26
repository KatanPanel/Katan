package me.devnatan.katan.webserver

import io.ktor.application.*
import io.ktor.features.*
import io.ktor.http.*
import io.ktor.locations.*
import io.ktor.serialization.*
import io.ktor.websocket.*
import kotlinx.serialization.json.Json
import me.devnatan.katan.common.util.get
import me.devnatan.katan.webserver.KatanWebServer.Companion.logger
import org.mpierce.ktor.csrf.CsrfProtection
import org.mpierce.ktor.csrf.OriginMatchesKnownHost

private val kws = KatanWebServer.INSTANCE
internal fun Application.installHooks() {
    install(DefaultHeaders)
    install(AutoHeadResponse)
    install(WebSockets)
    install(Locations)
    install(HSTS) {
        logger.info("Enabled Strict Transport Security`")
    }

    install(ContentNegotiation) {
        json(Json(DefaultJson) {
            prettyPrint = true
        })
    }

    install(CallLogging)

    install(CORS) {
        logger.info("Installing CORS feature...")
        allowCredentials = true

        val cors = kws.config.getConfig("security.cors")
        if (cors.get("allowAnyHost", false)) {
            logger.info("All hosts have been allowed through CORS.")
            anyHost()
        } else if (cors.hasPath("hosts")) {
            for (hostConfig in cors.getConfigList("hosts")) {
                host(
                    hostConfig.getString("hostname"),
                    hostConfig.get("schemes", emptyList()),
                    hostConfig.get("subDomains", emptyList())
                )
            }
            hosts.forEach { logger.info("Allowed $it host through CORS.") }
        }
    }

    install(StatusPages) {
        exception<Throwable> { cause ->
            call.response.status(HttpStatusCode.InternalServerError)
            throw cause
        }
    }

    install(CsrfProtection) {
        logger.info("Installing CSRF feature...")
        for (whitelist in kws.config.getConfigList("security.csrf.whitelist")) {
            val protocol = whitelist.getString("protocol")
            val hostname = whitelist.getString("hostname")
            val port = whitelist.getInt("port")
            validate(OriginMatchesKnownHost(protocol, hostname, port))
            logger.info("[CSRF] Allowed $protocol://$hostname:$port.")
        }
    }

}