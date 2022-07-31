dependencies {
    compileOnly(libs.ktor.server.core)
    compileOnly(libs.ktor.server.cio)
    runtimeOnly(libs.ktor.server.cio)
    implementation(projects.http.httpShared)
    implementation(projects.http.httpRoutes)
    implementation(projects.configuration)
}
