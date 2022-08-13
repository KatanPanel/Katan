dependencies {
    implementation(libs.koin.ktor)
    implementation(libs.ktor.server.feature.resources)
    implementation(libs.exposed.dao)
    implementation(libs.exposed.kotlin.datetime)
    implementation(libs.jedis)
    implementation(libs.hibernateValidator)
    implementation(projects.services.idService)
    implementation(projects.http.httpShared)
    testImplementation(projects.http.httpTest)
}
