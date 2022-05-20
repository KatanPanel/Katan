dependencies {
    compileOnly(project(":http-server:http-server-shared"))
    api(libs.ktor.client.feature.resources)
    api(libs.ktor.client.feature.content.negotiation)
    api(libs.ktor.serialization.json)
    api(libs.ktor.server.test)
}