package me.devnatan.katan

import io.ktor.http.HttpStatusCode
import io.ktor.server.testing.TestApplicationEngine
import io.ktor.server.testing.TestApplicationResponse
import io.ktor.server.testing.withTestApplication
import me.devnatan.katan.backend.main

fun testApp(callback: TestApplicationEngine.() -> Unit) {
    withTestApplication({
        this.main()
    }, callback)
}

val TestApplicationResponse.code: HttpStatusCode
    get() = status()!!