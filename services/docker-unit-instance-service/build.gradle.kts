dependencies {
    implementation(libs.dockerJava)
    implementation(libs.exposed.dao)
    implementation(projects.services.networkService)
    implementation(projects.services.idService)
    implementation(projects.configuration)
    implementation(projects.eventsDispatcher)
    implementation(projects.services.unitInstanceService)
}
