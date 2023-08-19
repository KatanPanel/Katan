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
    implementation(projects.http.httpShared)
    implementation(projects.configuration)
    implementation(libs.ktor.server.websockets)
    implementation(libs.log4j.slf4jImpl)
    implementation(libs.hibernateValidator)
    implementation(libs.hibernateValidator.cdi)
    implementation(libs.koin.core)
    implementation(libs.koin.ktor)
    implementation(libs.ktx.serialization.core)
    implementation(libs.ktx.atomicfu)
}
