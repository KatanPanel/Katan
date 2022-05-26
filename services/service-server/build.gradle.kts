dependencies {
    implementation(project(":services:service-container"))
    implementation(project(":http-server:http-server-shared"))
    testImplementation(project(":services:service-container"))
    testImplementation(project(":http-server:http-server-test"))
    compileOnly(libs.ktor.server.host.common)
    implementation(libs.ktor.server.feature.resources)
    implementation(libs.koin.ktor)
}