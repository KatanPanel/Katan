package org.katan.http.routes.unit

import io.ktor.client.call.body
import io.ktor.client.plugins.resources.post
import io.ktor.client.request.setBody
import io.ktor.http.HttpStatusCode
import io.ktor.server.routing.routing
import org.katan.http.HttpError
import org.katan.http.UnitMissingCreateOptions
import org.katan.http.createTestClient
import org.katan.http.routes.unit.dto.CreateUnitRequest
import org.katan.http.routes.unit.dto.CreateUnitResponse
import org.katan.http.routes.unit.locations.UnitResource
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
        val propName = "test"
        val request = testClient.post(UnitResource()) {
            setBody(
                CreateUnitRequest(
                    name = propName,
                    dockerImage = propName
                )
            )
        }

        val body = request.body<CreateUnitResponse>()
        assertEquals(HttpStatusCode.Created, request.status)
        assertEquals(propName, body.unit.name)
        assertEquals(propName, body.dockerImage)
    }

    @Test
    fun `given missing parameters when creating unit expect code 1003`() = withTestApplication({
        routing {
            createUnit()
        }
    }) {
        val testClient = createTestClient()
        val request = testClient.post(UnitResource())
        val body = request.body<HttpError>()

        assertEquals(HttpStatusCode.BadRequest, request.status)
        assertEquals(UnitMissingCreateOptions, body)
    }

}