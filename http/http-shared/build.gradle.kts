dependencies {
    implementation(projects.model)
    implementation(libs.ktor.server.websockets)
    implementation(libs.ktor.server.feature.defaultHeaders)
    implementation(libs.ktor.server.feature.autoHeadResponse)
    implementation(libs.ktor.server.feature.calllogging)
    implementation(libs.ktor.server.feature.resources)
    implementation(libs.ktor.server.feature.contentNegotiation)
    implementation(libs.ktor.server.feature.statuspages)
    implementation(libs.ktor.server.feature.cors)
    implementation(libs.ktor.server.feature.dataConversion)
    implementation(libs.ktor.serialization.kotlinx.json)
    implementation(libs.hibernateValidator)
}
