plugins {
    alias(libs.plugins.kotlin)
    alias(libs.plugins.kotlin.serialization)
    alias(libs.plugins.kotlinter)
}

repositories {
    mavenCentral()
}

dependencies {
    implementation(projects.core)
    implementation(projects.http.httpShared)
    implementation(projects.services.idService)
    implementation(projects.services.networkService)
    implementation(projects.services.fsService)
    implementation(projects.services.blueprintService)
    implementation(libs.hibernateValidator)
    implementation(libs.bundles.exposed)
    implementation(libs.koin.ktor)
    implementation(libs.ktor.server.auth)
    implementation(libs.ktor.server.feature.resources)
    implementation(libs.ktor.serialization.kotlinx.json)
    implementation(libs.yoki)
    implementation(libs.log4j.core)
    testImplementation(projects.http.httpTest)
}