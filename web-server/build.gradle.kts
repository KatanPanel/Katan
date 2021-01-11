val ktorVersion = "1.4.0"

repositories {
    maven("https://dl.bintray.com/marshallpierce/maven")
}

dependencies {
    compileOnly(project(":common"))
    implementation("io.ktor:ktor-locations:$ktorVersion")
    implementation("io.ktor:ktor-auth-jwt:$ktorVersion")
    implementation("io.ktor:ktor-server-jetty:$ktorVersion")
    implementation("io.ktor:ktor-server-core:$ktorVersion")
    implementation("io.ktor:ktor-websockets:$ktorVersion")
    implementation("io.ktor:ktor-jackson:$ktorVersion")
    implementation("com.fasterxml.jackson.core:jackson-databind:2.11.2")
    implementation("com.auth0:java-jwt:3.10.3")
    testImplementation("io.ktor:ktor-server-test-host:$ktorVersion")
}