package org.katan.service.server.http

import io.ktor.client.call.body
import io.ktor.client.plugins.resources.post
import io.ktor.client.request.setBody
import io.ktor.http.HttpStatusCode
import io.ktor.server.routing.routing
import org.katan.http.response.HttpError
import org.katan.http.test.createTestClient
import org.katan.http.test.withTestApplication
import org.katan.service.unit.http.UnitRoutes
import org.katan.service.unit.http.dto.CreateUnitRequest
import org.katan.service.unit.http.dto.UnitResponse
import org.katan.service.unit.http.routes.createUnit
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
        val propName = "test"
        val request = testClient.post(UnitRoutes()) {
            setBody(
                CreateUnitRequest(
                    name = propName,
                    image = propName,
                    network = null
                )
            )
        }

        val body = request.body<UnitResponse>()
        assertEquals(HttpStatusCode.Created, request.status)
        assertEquals(propName, body.name)
    }

    @Test
    fun `given missing parameters when creating unit expect code 1003`() = withTestApplication({
        routing {
            createUnit()
        }
    }) {
        val testClient = createTestClient()
        val request = testClient.post(UnitRoutes())
        val body = request.body<HttpError>()

        assertEquals(HttpStatusCode.BadRequest, request.status)
//        assertEquals(UnitMissingCreateOptions, body)
    }
}
