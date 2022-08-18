dependencies {
    implementation(libs.koin.ktor)
    implementation(libs.ktor.server.auth)
    implementation(libs.ktor.server.feature.resources)
    implementation(libs.hibernateValidator)
    implementation(projects.configuration)
    implementation(projects.http.httpShared)
    implementation(projects.services.idService)
    testImplementation(projects.http.httpTest)
}
