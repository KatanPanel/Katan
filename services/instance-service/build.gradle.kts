dependencies {
    implementation(libs.hibernateValidator)
    implementation(libs.koin.ktor)
    implementation(libs.ktor.server.auth)
    implementation(libs.ktor.server.feature.resources)
    implementation(libs.ktor.serialization.kotlinx.json)
    implementation(projects.configuration)
    implementation(projects.http.httpShared)
    implementation(projects.services.idService)
    implementation(projects.services.fsService)
    testImplementation(projects.http.httpTest)
}
