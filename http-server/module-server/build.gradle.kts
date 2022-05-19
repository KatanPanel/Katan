plugins {
    alias(libs.plugins.serialization)
}

dependencies {
    implementation(project(":http-server:http-server-shared"))
    implementation(project(":services:service-server"))
    testImplementation(project(":http-server:http-server-shared"))
    testImplementation(libs.ktor.client.feature.resources)
    testImplementation(libs.ktor.client.feature.content.negotiation)
    testImplementation(libs.ktor.serialization.json)
}