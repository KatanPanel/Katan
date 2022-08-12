dependencies {
    compileOnly(libs.ktor.server.core)
    compileOnly(libs.ktor.server.cio)
    runtimeOnly(libs.ktor.server.cio)
    implementation(projects.http.httpShared)
    implementation(projects.configuration)
    implementation("org.apache.logging.log4j:log4j-slf4j-impl:2.18.0")
    implementation(libs.hibernateValidator)
    implementation(libs.hibernateValidator.cdi)
    runtimeOnly(libs.expressly)
}
