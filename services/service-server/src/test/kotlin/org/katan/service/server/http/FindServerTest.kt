package org.katan.service.server.http

import io.ktor.client.plugins.resources.get
import io.ktor.http.HttpStatusCode
import io.ktor.server.routing.routing
import org.katan.http.createTestClient
import org.katan.http.withTestApplication
import org.katan.service.server.ServerCreateOptions
import org.katan.service.server.ServerService
import org.katan.service.server.http.locations.Servers
import org.katan.service.server.http.routes.createServer
import org.katan.service.server.http.routes.findServer
import org.koin.test.KoinTest
import org.koin.test.get
import kotlin.test.Test
import kotlin.test.assertEquals

class FindServerTest : KoinTest {

    @Test
    fun `when server is not found expect 400`() = withTestApplication({
        routing {
            findServer()
        }
    }) {
        val testClient = createTestClient()
        val request = testClient.get(Servers.Get(id = "unknown"))

        assertEquals(HttpStatusCode.BadRequest, request.status)
    }

}