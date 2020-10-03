val ktorVersion = "1.4.0"

repositories {
    maven("https://dl.bintray.com/marshallpierce/maven")
}

dependencies {
    implementation(project(":common"))
    implementation("io.ktor:ktor-locations:$ktorVersion")
    implementation("io.ktor:ktor-auth-jwt:$ktorVersion")
    implementation("io.ktor:ktor-server-jetty:$ktorVersion")
    implementation("io.ktor:ktor-server-core:$ktorVersion")
    implementation("io.ktor:ktor-websockets:$ktorVersion")
    implementation("io.ktor:ktor-jackson:$ktorVersion")
    implementation("io.ktor:ktor-metrics:$ktorVersion")
    implementation("io.ktor:ktor-metrics-micrometer:$ktorVersion")
    implementation("io.micrometer:micrometer-registry-prometheus:1.5.5")
    implementation("org.mpierce.ktor.csrf:ktor-csrf:0.5.0")
    implementation("com.fasterxml.jackson.core:jackson-databind:2.11.2")
    implementation("com.auth0:java-jwt:3.10.3")
}