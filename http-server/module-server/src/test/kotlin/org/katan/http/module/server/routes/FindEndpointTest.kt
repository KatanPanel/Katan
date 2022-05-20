package org.katan.http.module.server.routes

import io.ktor.client.plugins.resources.get
import io.ktor.http.HttpStatusCode
import io.ktor.server.routing.routing
import org.katan.http.createTestClient
import org.katan.http.module.server.locations.Servers
import kotlin.test.Test
import kotlin.test.assertEquals
import org.katan.http.withTestApplication

class FindEndpointTest {

    @Test
    fun `return 400 on missing parameters`() = withTestApplication(setup = {
        routing {
            findServer()
        }
    }) {
        val testClient = createTestClient()
        val request = testClient.get(Servers.Get(id = "99"))

        assertEquals(HttpStatusCode.BadRequest, request.status)
    }

}