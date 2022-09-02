package org.katan.http.client

import io.ktor.client.HttpClient
import io.ktor.client.engine.cio.CIO

fun createHttpClient(): HttpClient {
    return HttpClient(CIO) {}
}