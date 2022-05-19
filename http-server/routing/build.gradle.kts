plugins {
    alias(libs.plugins.serialization)
}

dependencies {
    implementation(libs.ktor.server.core)
    implementation(libs.ktor.server.feature.resources)
//    compileOnly(libs.ktor.server.host.common)
}