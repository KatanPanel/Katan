package org.katan.http.module.server.routes

import io.ktor.client.plugins.resources.post
import io.ktor.http.HttpStatusCode
import io.ktor.server.routing.routing
import org.katan.http.createTestClient
import org.katan.http.module.server.locations.Servers
import org.katan.http.withTestApplication
import org.katan.service.container.FakeContainerFactory
import org.katan.service.server.ServerService
import org.katan.service.server.ServerServiceMock
import org.koin.test.KoinTest
import kotlin.test.Test
import kotlin.test.assertEquals

class CreateEndpointTest : KoinTest {

    @Test
    fun `should respond bad request on missing parameters`() = withTestApplication(
        di = {
            single<ServerService> { ServerServiceMock(FakeContainerFactory()) }
        },
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