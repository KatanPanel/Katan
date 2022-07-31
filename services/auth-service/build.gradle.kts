dependencies {
    implementation(libs.javaJwt)
    implementation(libs.bcprov)
    implementation(libs.koin.ktor)
    implementation(libs.ktor.server.feature.resources)
    implementation(libs.ktor.server.auth)
    implementation(libs.ktor.server.auth.jwt)
    implementation(projects.services.accountService)
    implementation(projects.http.httpShared)
    testImplementation(projects.http.httpTest)
}

configurations {
    implementation {
        exclude(module = "bcpkix-jdk15on")
        exclude(module = "bcprov-jdk15on")
    }
}