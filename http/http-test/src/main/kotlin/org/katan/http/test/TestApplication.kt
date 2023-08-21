package org.katan.http.test

import io.ktor.client.HttpClient
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.client.plugins.defaultRequest
import io.ktor.client.plugins.resources.Resources
import io.ktor.client.request.headers
import io.ktor.http.ContentType
import io.ktor.http.URLProtocol
import io.ktor.http.contentType
import io.ktor.serialization.kotlinx.json.json
import io.ktor.server.application.Application
import io.ktor.server.testing.ApplicationTestBuilder
import io.ktor.server.testing.testApplication
import kotlinx.serialization.json.Json
import org.katan.http.installDefaultFeatures

val json: Json = Json { ignoreUnknownKeys = true }

inline fun withTestApplication(
    noinline setup: Application.() -> Unit,
    crossinline block: suspend ApplicationTestBuilder.() -> Unit = {}
) {
    testApplication {
        application {
            installDefaultFeatures(isDevelopmentMode = true, json = json)
            setup()
        }

        block()
    }
}

fun ApplicationTestBuilder.createTestClient(
    block: HttpClient.() -> Unit = {}
): HttpClient {
    return createClient {
        install(Resources)
        install(ContentNegotiation) {
            json()
        }

        defaultRequest {
            host = "localhost"
            port = 80
            url { protocol = URLProtocol.HTTP }
            headers {
                contentType(ContentType.Application.Json)
            }
        }
    }.apply { block(this) }
}
