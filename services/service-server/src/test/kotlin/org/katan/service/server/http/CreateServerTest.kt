package org.katan.service.server.http

import io.ktor.client.plugins.resources.post
import io.ktor.client.request.setBody
import io.ktor.http.HttpStatusCode
import io.ktor.server.routing.routing
import org.katan.http.createTestClient
import org.katan.http.withTestApplication
import org.katan.service.server.ServerCreateOptions
import org.katan.service.server.http.locations.Servers
import org.katan.service.server.http.routes.createServer
import org.koin.test.KoinTest
import kotlin.test.Test
import kotlin.test.assertEquals

class CreateServerTest : KoinTest {

    @Test
    fun `should return 201 on server create`() = withTestApplication({
        routing {
            createServer()
        }
    }) {
        val testClient = createTestClient()
        val request = testClient.post(Servers()) {
            setBody(ServerCreateOptions("test"))
        }

        assertEquals(HttpStatusCode.Created, request.status)
    }

    @Test
    fun `given missing parameters when creating server expect 400`() = withTestApplication({
        routing {
            createServer()
        }
    }) {
        val testClient = createTestClient()
        val request = testClient.post(Servers())

        assertEquals(HttpStatusCode.BadRequest, request.status)
    }

}