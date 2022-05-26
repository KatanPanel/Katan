package org.katan.http

import io.ktor.client.HttpClient
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.client.plugins.defaultRequest
import io.ktor.client.plugins.resources.Resources
import io.ktor.http.URLProtocol
import io.ktor.serialization.kotlinx.json.json
import io.ktor.server.application.Application
import io.ktor.server.testing.ApplicationTestBuilder
import io.ktor.server.testing.testApplication
import org.koin.core.context.startKoin

public fun withTestApplication(
    setup: Application.() -> Unit,
    block: suspend ApplicationTestBuilder.() -> Unit = {}
) {
    startKoin {
        testApplication {
            application {
                installDefaultServerFeatures()
                setup()
            }

            block()
        }
    }
}

public fun ApplicationTestBuilder.createTestClient(
    block: (HttpClient.() -> Unit)? = null,
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
    }.apply { block?.invoke(this) }
}