package org.katan.http.module.server

import io.ktor.client.HttpClient
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.client.plugins.resources.Resources
import io.ktor.serialization.kotlinx.json.json
import io.ktor.server.testing.ApplicationTestBuilder
import io.ktor.server.testing.testApplication
import org.katan.http.installDefaultFeatures

fun withTestApplication(
    clientApplier: (HttpClient.() -> Unit)? = null,
    testBlock: suspend ApplicationTestBuilder.(HttpClient) -> Unit
) = testApplication {
    application {
        installDefaultFeatures()
    }

    val client = createClient {
        install(Resources)
        install(ContentNegotiation) {
            json()
        }
    }.apply { clientApplier?.invoke(this) }

    testBlock(client)
}