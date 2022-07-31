dependencies {
    implementation(libs.koin.ktor)
    implementation(libs.ktor.server.feature.resources)
    implementation(projects.configuration)
    implementation(projects.services.idService)
    implementation(projects.services.unitInstanceService)
    implementation(projects.http.httpShared)
    testImplementation(projects.http.httpTest)
}
