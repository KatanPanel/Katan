package org.katan.http

import io.ktor.client.HttpClient
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.client.plugins.defaultRequest
import io.ktor.client.plugins.resources.Resources
import io.ktor.client.request.headers
import io.ktor.http.ContentType
import io.ktor.http.URLProtocol
import io.ktor.http.contentType
import io.ktor.serialization.jackson.jackson
import io.ktor.server.application.Application
import io.ktor.server.testing.ApplicationTestBuilder
import io.ktor.server.testing.testApplication
import org.koin.dsl.module
import org.koin.test.KoinTest
import org.koin.test.mock.declare

fun withTestApplication(
    setup: Application.() -> Unit,
    block: suspend ApplicationTestBuilder.() -> Unit = {}
) {
    testApplication {
        application {
            installDefaultFeatures()
            setup()
        }

        block()
    }
}

fun ApplicationTestBuilder.createTestClient(
    block: (HttpClient.() -> Unit)? = null,
): HttpClient {
    return createClient {
        install(Resources)
        install(ContentNegotiation) {
            jackson()
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