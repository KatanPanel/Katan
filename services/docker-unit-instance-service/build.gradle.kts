dependencies {
    implementation(projects.services.unitInstanceService)
    implementation(projects.services.networkService)
    implementation(projects.configuration)
    implementation(libs.dockerJava)
    implementation(libs.dockerJava.transport.okhttp)
}