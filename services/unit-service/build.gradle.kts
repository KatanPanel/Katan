plugins {
    alias(libs.plugins.kotlin)
}

repositories {
    mavenCentral()
}

dependencies {
    implementation(projects.configuration)
    implementation(projects.eventsDispatcher)
    implementation(projects.services.authService)
    implementation(projects.services.idService)
    implementation(projects.services.idService)
    implementation(projects.services.instanceService)
    implementation(projects.services.accountService)
    implementation(projects.services.blueprintService)
    implementation(projects.http.httpShared)
    implementation(projects.model)
    implementation(libs.koin.ktor)
    implementation(libs.ktor.server.auth)
    implementation(libs.ktor.server.feature.resources)
    implementation(libs.bundles.exposed)
    implementation(libs.hibernateValidator)
    testImplementation(projects.http.httpTest)
}

kotlin {
    explicitApi()
}
