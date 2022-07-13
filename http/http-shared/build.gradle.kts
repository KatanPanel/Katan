dependencies {
    implementation(libs.ktor.server.feature.defaultheaders)
    implementation(libs.ktor.server.feature.autoheadresponse)
    implementation(libs.ktor.server.feature.calllogging)
    implementation(libs.ktor.server.feature.resources)
    implementation(libs.ktor.server.feature.content.negotiation)
    implementation(libs.ktor.server.feature.statuspages)
    implementation(libs.ktor.serialization.kotlinx.json)
}