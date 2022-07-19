package org.katan.http.routes.unit

import io.ktor.client.plugins.resources.post
import io.ktor.client.request.setBody
import io.ktor.http.HttpStatusCode
import io.ktor.server.routing.routing
import org.katan.http.createTestClient
import org.katan.http.routes.unit.dto.CreateUnitRequest
import org.katan.http.routes.unit.locations.UnitRoutes
import org.katan.http.routes.unit.routes.createUnit
import org.katan.http.withTestApplication
import org.koin.test.KoinTest
import kotlin.test.Test
import kotlin.test.assertEquals

class CreateUnitTest : KoinTest {

    @Test
    fun `should return 201 on unit successful create`() = withTestApplication({
        routing {
            createUnit()
        }
    }) {
        val testClient = createTestClient()
        val request = testClient.post(UnitRoutes()) {
            setBody(
                CreateUnitRequest(
                    name = "test",
                    dockerImage = "test"
                )
            )
        }

        assertEquals(HttpStatusCode.Created, request.status)
    }

    @Test
    fun `given missing parameters when creating unit expect 400`() = withTestApplication({
        routing {
            createUnit()
        }
    }) {
        val testClient = createTestClient()
        val request = testClient.post(UnitRoutes())

        assertEquals(HttpStatusCode.BadRequest, request.status)
    }

}