package org.katan.service.server.http

import io.ktor.client.plugins.resources.post
import io.ktor.http.HttpStatusCode
import io.ktor.server.routing.routing
import org.katan.http.createTestClient
import org.katan.http.withTestApplication
import org.katan.service.server.http.locations.Servers
import org.katan.service.server.http.routes.createServer
import org.koin.test.KoinTest
import kotlin.test.Test
import kotlin.test.assertEquals

class CreateEndpointTest : KoinTest {

    @Test
    fun `should respond bad request on missing parameters`() = withTestApplication(
        setup = {
            routing {
                createServer()
            }
        }
    ) {
        val testClient = createTestClient()
        val request = testClient.post(Servers())

        assertEquals(HttpStatusCode.BadRequest, request.status)
    }

}