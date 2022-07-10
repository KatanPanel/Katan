package org.katan.http.routes.unit

import io.ktor.client.plugins.resources.get
import io.ktor.http.HttpStatusCode
import io.ktor.server.routing.routing
import org.katan.http.createTestClient
import org.katan.http.routes.unit.locations.UnitRoutes
import org.katan.http.routes.unit.routes.findServer
import org.katan.http.withTestApplication
import org.koin.test.KoinTest
import kotlin.test.Test
import kotlin.test.assertEquals

class FindUnitTest : KoinTest {

    @Test
    fun `when server is not found expect 400`() = withTestApplication({
        routing {
            findServer()
        }
    }) {
        val testClient = createTestClient()
        val request = testClient.get(UnitRoutes.Get(id = "unknown"))

        assertEquals(HttpStatusCode.BadRequest, request.status)
    }

}