package org.katan.http.module.server.routes

import io.ktor.client.plugins.resources.get
import io.ktor.http.HttpStatusCode
import io.ktor.server.routing.routing
import org.katan.http.createTestClient
import org.katan.http.module.server.locations.Servers
import org.katan.http.withTestApplication
import org.katan.service.container.FakeContainerFactory
import org.katan.service.server.Server
import org.katan.service.server.ServerCreateOptions
import org.katan.service.server.ServerService
import org.katan.service.server.ServerServiceMock
import org.koin.dsl.module
import org.koin.test.KoinTest
import org.koin.test.get
import kotlin.test.Test
import kotlin.test.assertEquals

class FindEndpointTest : KoinTest {

    @Test
    fun `return 400 when server is not found`() = withTestApplication(
        di = {
            single<ServerService> { ServerServiceMock(FakeContainerFactory()) }
        },
        setup = {
            routing {
                findServer()
            }
        }
    ) {
        val testClient = createTestClient()
        val request = testClient.get(Servers.Get(id = "unknown"))

        assertEquals(HttpStatusCode.BadRequest, request.status)
    }

    @Test
    fun `return 200 when server is found`() = withTestApplication(
        di = {
            single<ServerService> { ServerServiceMock(FakeContainerFactory()) }
        },
        setup = {
            routing {
                createServer()
            }
        }
    ) {
        val serverService = get<ServerService>()
        serverService.create(ServerCreateOptions("unknown"))

        val testClient = createTestClient()
        val request = testClient.get(Servers.Get(id = "unknown"))

        assertEquals(HttpStatusCode.BadRequest, request.status)
    }

}