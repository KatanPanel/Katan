dependencies {
    implementation(projects.services.unitService)
    implementation(projects.services.idService)
    implementation(projects.http.httpShared)
    testImplementation(projects.http.httpTest)
}