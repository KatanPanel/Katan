val exposedVersion = "0.27.1"

dependencies {
    api(project(":common"))
    implementation("org.jetbrains.exposed:exposed-core:$exposedVersion")
    implementation("org.jetbrains.exposed:exposed-dao:$exposedVersion")
    implementation("org.jetbrains.exposed:exposed-java-time:$exposedVersion")
    implementation("org.jetbrains.exposed:exposed-jdbc:$exposedVersion")
    implementation("com.auth0:java-jwt:3.10.3")
    implementation("org.jetbrains.kotlinx:atomicfu:0.14.4")
    implementation("com.github.docker-java:docker-java:3.2.5")
    implementation("com.h2database:h2:1.4.200")
    implementation("mysql:mysql-connector-java:8.0.21")
}