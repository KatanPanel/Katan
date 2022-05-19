dependencies {
    compileOnly(libs.ktor.server.core)
    compileOnly(libs.ktor.server.netty)
    runtimeOnly(libs.ktor.server.netty)
    implementation(project(":http-server:http-server-shared"))
    implementation(project(":http-server:module-server"))
}