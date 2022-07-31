dependencies {
    implementation(projects.services.unitService)
    implementation(projects.services.unitInstanceService)
    implementation(projects.services.idService)
    implementation(projects.services.authService)
    implementation(projects.http.httpShared)
    testImplementation(projects.http.httpTest)
}
