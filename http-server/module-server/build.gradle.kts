dependencies {
    implementation(project(":services:service-server"))
    implementation(project(":http-server:http-server-shared"))
    testImplementation(project(":http-server:http-server-test"))
    testImplementation(project(":services:service-container"))
}