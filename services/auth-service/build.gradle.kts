plugins {
    alias(libs.plugins.kotlin)
    alias(libs.plugins.kotlin.serialization)
}

repositories {
    mavenCentral()
}

dependencies {
    implementation(projects.core)
    implementation(projects.services.accountService)
    implementation(projects.http.httpShared)
    implementation(libs.javaJwt)
    implementation(libs.koin.ktor)
    implementation(libs.ktor.server.feature.resources)
    implementation(libs.ktor.server.auth)
    implementation(libs.ktor.server.auth.jwt)
    implementation(libs.hibernateValidator)
    implementation(libs.ktx.datetime)
    testImplementation(projects.http.httpTest)
}

kotlin {
    explicitApi()
}
