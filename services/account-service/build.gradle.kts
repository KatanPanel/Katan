plugins {
    alias(libs.plugins.kotlin)
    alias(libs.plugins.kotlin.serialization)
}

repositories {
    mavenCentral()
}

dependencies {
    implementation(projects.crypto)
    implementation(projects.services.idService)
    implementation(projects.http.httpShared)
    implementation(projects.eventsDispatcher)
    implementation(projects.model)
    implementation(libs.koin.ktor)
    implementation(libs.ktor.server.feature.resources)
    implementation(libs.exposed.dao)
    implementation(libs.exposed.kotlin.datetime)
    implementation(libs.hibernateValidator)
    testImplementation(projects.http.httpTest)
}
