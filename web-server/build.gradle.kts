val ktorVersion = "1.4.0"

dependencies {
    api(project(":common"))
    implementation("io.ktor:ktor-locations:$ktorVersion")
    implementation("io.ktor:ktor-auth-jwt:$ktorVersion")
    implementation("io.ktor:ktor-server-netty:$ktorVersion")
    implementation("io.ktor:ktor-server-core:$ktorVersion")
    implementation("io.ktor:ktor-websockets:$ktorVersion")
    implementation("io.ktor:ktor-serialization:$ktorVersion")
    implementation("org.mpierce.ktor.csrf:ktor-csrf:0.5.0")
    implementation("org.jetbrains.kotlinx:kotlinx-serialization-json:1.0.0-RC2")
}