package org.katan.http.routes.unit

import io.ktor.client.call.body
import io.ktor.client.plugins.resources.get
import io.ktor.http.HttpStatusCode
import io.ktor.server.routing.routing
import org.katan.http.HttpError
import org.katan.http.InvalidUnitIdFormat
import org.katan.http.UnitNotFound
import org.katan.http.createTestClient
import org.katan.http.routes.unit.routes.findUnit
import org.katan.http.test.VALID_SNOWFLAKE_ID
import org.katan.http.withTestApplication
import org.koin.test.KoinTest
import kotlin.test.Test
import kotlin.test.assertEquals

class FindUnitTest : KoinTest {

    @Test
    fun `when unit is not found expect error 1001`() = withTestApplication({
        routing {
            findUnit()
        }
    }) {
        val testClient = createTestClient()
        val request = testClient.get(UnitResource.ById(id = VALID_SNOWFLAKE_ID))
        val body = request.body<HttpError>()

        assertEquals(HttpStatusCode.BadRequest, request.status)
        assertEquals(UnitNotFound, body)
    }

    @Test
    fun `when incorrect unit id expect error 1004`() = withTestApplication({
        routing {
            findUnit()
        }
    }) {
        val testClient = createTestClient()
        val request = testClient.get(UnitResource.ById(id = "abcdefgh"))
        val body = request.body<HttpError>()

        assertEquals(HttpStatusCode.BadRequest, request.status)
        assertEquals(InvalidUnitIdFormat, body)
    }
}
