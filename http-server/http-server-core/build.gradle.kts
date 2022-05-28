dependencies {
    compileOnly(libs.ktor.server.core)
    compileOnly(libs.ktor.server.cio)
    runtimeOnly(libs.ktor.server.cio)
    implementation(project(":http-server:http-server-shared"))
}