package org.katan.http.module.server.routes

import io.ktor.client.plugins.resources.post
import io.ktor.http.HttpStatusCode
import io.ktor.server.routing.routing
import org.katan.http.createTestClient
import org.katan.http.module.server.locations.Servers
import org.katan.http.withTestApplication
import kotlin.test.Test
import kotlin.test.assertEquals

class CreateEndpointTest {

    @Test
    fun `should respond bad request on missing parameters`() = withTestApplication(setup = {
        routing {
            createServer()
        }
    }) {
        val testClient = createTestClient()
        val request = testClient.post(Servers.Create())

        println(request)
        assertEquals(HttpStatusCode.BadRequest, request.status)
    }

}