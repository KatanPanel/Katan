dependencies {
    compileOnly(libs.ktor.server.core)
    compileOnly(libs.ktor.server.cio)
    runtimeOnly(libs.ktor.server.cio)
    implementation(libs.log4j.slf4jImpl)
    implementation(projects.http.httpShared)
    implementation(projects.configuration)
    implementation(libs.hibernateValidator)
    implementation(libs.hibernateValidator.cdi)
    runtimeOnly(libs.expressly)
}
