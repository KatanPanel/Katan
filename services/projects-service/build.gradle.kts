plugins {
    alias(libs.plugins.kotlin)
    alias(libs.plugins.kotlin.serialization)
}

repositories {
    mavenCentral()
}

dependencies {
    implementation(projects.model)
    implementation(projects.eventsDispatcher)
    implementation(projects.http.httpShared)
    implementation(libs.koin.core)
    implementation(libs.koin.ktor)
    implementation(libs.ktx.datetime)
    implementation(libs.bundles.exposed)
    implementation(libs.ktor.server.auth)
    implementation(libs.ktor.server.feature.resources)
}

kotlin {
    explicitApi()
}