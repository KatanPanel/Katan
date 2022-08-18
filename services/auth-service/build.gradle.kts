dependencies {
    implementation(libs.javaJwt)
    implementation(libs.koin.ktor)
    implementation(libs.ktor.server.feature.resources)
    implementation(libs.ktor.server.auth)
    implementation(libs.ktor.server.auth.jwt)
    implementation(libs.hibernateValidator)
    implementation(projects.crypto)
    implementation(projects.services.accountService)
    implementation(projects.http.httpShared)
    testImplementation(projects.http.httpTest)
}
