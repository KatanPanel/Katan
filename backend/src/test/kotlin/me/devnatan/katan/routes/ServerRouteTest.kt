package me.devnatan.katan.routes

import io.ktor.http.*
import io.ktor.server.testing.handleRequest
import io.ktor.server.testing.setBody
import me.devnatan.katan.backend.util.asJsonString
import me.devnatan.katan.code
import me.devnatan.katan.testApp
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test

class ServerRouteTest {

    @Test
    fun `get unknown server expect 404 status code`(): Unit = testApp {
        with(handleRequest(HttpMethod.Get, "/servers/9999")) {
            assertEquals(HttpStatusCode.NotFound, response.code)
        }
    }

    @Test
    fun `add server with missing data expect 400 status code`(): Unit = testApp {
        with(handleRequest(HttpMethod.Post, "/servers") {
            addHeader(HttpHeaders.ContentType, ContentType.Application.FormUrlEncoded.toString())
            setBody(listOf(
                "serverName" to "Testing"
            ).formUrlEncode())
        }) {
            assertEquals(HttpStatusCode.BadRequest, response.code)
        }
    }

    @Test
    fun `add server expect 201 or 409 status code`(): Unit = testApp {
        val body = listOf(
            "serverName" to "Testing",
            "address" to "127.0.0.1",
            "port" to "25565",
            "memory" to "1024"
        )
        with(handleRequest(HttpMethod.Post, "/servers") {
            addHeader(HttpHeaders.ContentType, ContentType.Application.FormUrlEncoded.toString())
            setBody(body.formUrlEncode())
        }) {
            if (response.code == HttpStatusCode.Created) {
                val content = Parameters.build {
                    for (pair in body) {
                        append(pair.first, pair.second)
                    }
                }.formUrlEncode()

                assertEquals(response.content, content)
            } else {
                assertEquals(HttpStatusCode.Conflict, response.code)
            }
        }
    }

    @Test
    fun `add server that already exists expect 409 status code`(): Unit = testApp {
        with(handleRequest(HttpMethod.Post, "/servers") {
            addHeader(HttpHeaders.ContentType, ContentType.Application.Json.toString())
            setBody(mapOf(
                "serverName" to "Testing",
                "address" to "127.0.0.1",
                "port" to 25565,
                "memory" to 1024
            ).asJsonString()!!)
        }) {
            assertEquals(HttpStatusCode.Conflict, response.code)
        }
    }

}