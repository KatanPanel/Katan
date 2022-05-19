dependencies {
    compileOnly(libs.ktor.server.core)
    compileOnly(libs.ktor.server.host.common)
    compileOnly(libs.ktor.server.netty)
    runtimeOnly(libs.ktor.server.netty)
    implementation(project(":http-server:routing"))
    implementation(libs.ktor.server.feature.defaultheaders)
    implementation(libs.ktor.server.feature.autoheadresponse)
    implementation(libs.ktor.server.feature.calllogging)
    implementation(libs.ktor.server.feature.resources)
}