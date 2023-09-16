plugins {
    alias(libs.plugins.kotlin)
    alias(libs.plugins.kotlin.serialization)
}

repositories {
    mavenCentral()
}

dependencies {
    runtimeOnly(libs.ktor.server.cio)
    runtimeOnly(libs.expressly)
    compileOnly(libs.ktor.server.core)
    compileOnly(libs.ktor.server.cio)
    implementation(projects.core)
    implementation(projects.http.httpShared)
    implementation(libs.ktor.server.websockets)
    implementation(libs.hibernateValidator)
    implementation(libs.hibernateValidator.cdi)
    implementation(libs.koin.core)
    implementation(libs.koin.ktor)
    implementation(libs.ktx.serialization.core)
    implementation(libs.ktx.serialization.json)
    implementation(libs.ktx.atomicfu)
    implementation(libs.log4j.core)
}
