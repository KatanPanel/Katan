dependencies {
    api(libs.ktor.client.core)
    implementation(libs.ktor.client.okhttp)
    implementation(libs.ktor.client.feature.content.negotiation)
    implementation(libs.ktor.serialization.kotlinx.json)
}
