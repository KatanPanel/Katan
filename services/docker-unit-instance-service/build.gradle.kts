dependencies {
    implementation(libs.exposed.dao)
    implementation(projects.dockerClient)
    implementation(projects.configuration)
    implementation(projects.eventsDispatcher)
    implementation(projects.services.networkService)
    implementation(projects.services.idService)
    implementation(projects.services.unitInstanceService)
}
