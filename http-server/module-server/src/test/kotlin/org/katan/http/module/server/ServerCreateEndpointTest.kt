package org.katan.http.module.server

import io.ktor.client.plugins.resources.post
import io.ktor.http.ContentType
import io.ktor.http.HttpStatusCode
import io.ktor.http.contentType
import org.katan.http.module.server.locations.Servers
import org.katan.http.module.server.routes.createServer
import kotlin.test.Test
import kotlin.test.assertEquals

class ServerCreateEndpointTest {

    @Test
    fun `should respond bad request on missing parameters`() = withTestApplication { client ->
        routing {
            createServer()
        }

        val request = client.post(Servers.Create()) {
            contentType(ContentType.Application.Json)
        }

        assertEquals(HttpStatusCode.BadRequest, request.status)
    }

}