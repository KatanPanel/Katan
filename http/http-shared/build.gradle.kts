plugins {
    alias(libs.plugins.kotlin)
    alias(libs.plugins.kotlin.serialization)
}

repositories {
    mavenCentral()
}

dependencies {
    implementation(projects.core)
    implementation(libs.koin.core)
    implementation(libs.ktor.server.websockets)
    implementation(libs.ktor.server.feature.defaultHeaders)
    implementation(libs.ktor.server.feature.autoHeadResponse)
    implementation(libs.ktor.server.feature.calllogging)
    implementation(libs.ktor.server.feature.resources)
    implementation(libs.ktor.server.feature.contentNegotiation)
    implementation(libs.ktor.server.feature.statuspages)
    implementation(libs.ktor.server.feature.cors)
    implementation(libs.ktor.server.feature.dataConversion)
    implementation(libs.ktor.serialization.kotlinx.json)
    implementation(libs.hibernateValidator)
    implementation(libs.ktx.atomicfu)
    implementation(libs.log4j.core)
}
