dependencies {
    implementation(project(":services:service-container"))
    implementation(project(":http:http-shared"))
    testImplementation(project(":services:service-container"))
    testImplementation(project(":http:http-test"))
    compileOnly(libs.ktor.server.host.common)
    implementation(libs.ktor.server.feature.resources)
    implementation(libs.koin.ktor)
}