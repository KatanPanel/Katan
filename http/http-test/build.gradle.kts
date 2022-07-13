dependencies {
    compileOnly(project(":http:http-shared"))
    api(libs.ktor.client.feature.resources)
    api(libs.ktor.client.feature.content.negotiation)
    api(libs.ktor.server.test)
    api(libs.koin.test)
    implementation(libs.ktor.serialization.jackson)
}