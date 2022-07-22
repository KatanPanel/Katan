dependencies {
    implementation(projects.services.unitInstanceService)
    implementation(projects.services.networkService)
    implementation(projects.services.idService)
    implementation(projects.configuration)
    implementation(libs.dockerJava)
    implementation(libs.dockerJava.transport.okhttp)
    implementation(projects.eventsDispatcher)
}