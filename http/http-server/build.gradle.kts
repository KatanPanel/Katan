dependencies {
    compileOnly(libs.ktor.server.core)
    compileOnly(libs.ktor.server.cio)
    runtimeOnly(libs.ktor.server.cio)
    implementation(project(":http:http-shared"))
    implementation(project(":http:http-routes-server"))
}