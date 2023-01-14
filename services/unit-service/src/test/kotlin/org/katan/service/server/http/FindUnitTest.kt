package org.katan.service.server.http

import io.ktor.client.call.body
import io.ktor.client.plugins.resources.get
import io.ktor.http.HttpStatusCode
import io.ktor.server.routing.routing
import org.katan.http.response.HttpError
import org.katan.http.test.VALID_SNOWFLAKE_ID
import org.katan.http.test.createTestClient
import org.katan.http.test.withTestApplication
import org.katan.service.unit.http.UnitRoutes
import org.katan.service.unit.http.routes.getUnit
import org.koin.test.KoinTest
import kotlin.test.Test
import kotlin.test.assertEquals

class FindUnitTest : KoinTest {

    @Test
    fun `when unit is not found expect HTTP 400`() = withTestApplication({
        routing {
            getUnit()
        }
    }) {
        val testClient = createTestClient()
        val request = testClient.get(UnitRoutes.ById(unitId = VALID_SNOWFLAKE_ID))
        val body = request.body<HttpError>()

        assertEquals(HttpStatusCode.BadRequest, request.status)
        assertEquals(HttpError.UnknownUnit, body)
    }

    @Test
    fun `given incorrect unit id expect HTTP 400`() = withTestApplication({
        routing {
            getUnit()
        }
    }) {
        val testClient = createTestClient()
        val request = testClient.get(UnitRoutes.ById(unitId = "abcdefgh"))
        val body = request.body<HttpError>()

        assertEquals(HttpStatusCode.BadRequest, request.status)
        assertEquals(HttpError.UnknownUnit, body)
    }
}
