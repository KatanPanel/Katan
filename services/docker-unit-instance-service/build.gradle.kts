dependencies {
    implementation(project(":services:unit-instance-service"))
    implementation(libs.dockerJava)
    compileOnly(libs.dockerJava.transport.okhttp)
    runtimeOnly(libs.dockerJava.transport.okhttp)
}