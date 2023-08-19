plugins {
    alias(libs.plugins.kotlin)
}

repositories {
    mavenCentral()
}

dependencies {
    compileOnly(projects.http.httpShared)
    api(libs.ktor.client.feature.resources)
    api(libs.ktor.client.feature.content.negotiation)
    api(libs.ktor.server.test)
    api(libs.koin.test)
    implementation(libs.ktor.serialization.kotlinx.json)
}
