dependencies {
    implementation(libs.koin.ktor)
    implementation(libs.ktor.server.feature.resources)
    implementation(libs.exposed.dao)
    implementation(libs.exposed.kotlin.datetime)
    implementation(libs.jedis)
    implementation(projects.services.idService)
    implementation(libs.hibernateValidator)
    implementation(projects.http.httpShared)
    testImplementation(projects.http.httpTest)
}
