dependencies {
    api(libs.dockerJava.api)
    implementation(libs.dockerJava)
    implementation(libs.dockerJava.core)
    implementation(libs.dockerJava.transport.okhttp)
    implementation(projects.configuration)
}
