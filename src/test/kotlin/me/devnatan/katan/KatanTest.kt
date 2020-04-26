package me.devnatan.katan

import io.ktor.http.HttpStatusCode
import io.ktor.server.testing.TestApplicationEngine
import io.ktor.server.testing.TestApplicationResponse
import io.ktor.server.testing.withTestApplication

fun testApp(callback: TestApplicationEngine.() -> Unit) {
    withTestApplication({
        main()
    }, callback)
}

val TestApplicationResponse.code: HttpStatusCode
    get() = status()!!